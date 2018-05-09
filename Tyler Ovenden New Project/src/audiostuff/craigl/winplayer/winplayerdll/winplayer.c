// WinPlayer DLL
// Written by: Craig A. Lindley
// Last Update: 11/09/99
// Java native method interface DLL to Win32 wave stuff
// Designed for minimum latency

#include <stdio.h>
#include <windows.h>
#include "craigl_winplayer_WinPlayer.h"

typedef short AudioSample;

// ********************** IMPORTANT NOTE ********************************
// BUFFERSIZEINSAMPLES must be set carefully for this code to work
// correctly. Too large a value means more latency. Too small a value
// will cause breakup in the sound. In all cases this value must be
// smaller than AudioConstants.SAMPLEBUFFERSIZE
// **********************************************************************

#define BUFFERSIZEINSAMPLES 7500	// Set to appropriate size. See note above
#define NUMBEROFBUFFERS        4	// Number of buffers to utilize
#define BITSPERSAMPLE         16	// Utilize 16 bit samples
#define BUFFERSIZE (BUFFERSIZEINSAMPLES * sizeof(AudioSample))

// Globals in this DLL
static HWAVEOUT hDevice;            // Windows audio device to open
static HANDLE hEvent;				// Event used to signal audio data ready
static HGLOBAL hMem;				// Handle to buffer memory
static LPBYTE pMem;					// Ptr to buffer memory
static WAVEHDR waveHdr[NUMBEROFBUFFERS];// Wave headers
static int currentBufferIndex;		// Index of current buffer

// Forward reference
void printErrorMessage(char *msg, MMRESULT status);

// nativeSelectDevice - Select a device capable of playing our wave data
JNIEXPORT jboolean 
JNICALL Java_craigl_winplayer_WinPlayer_nativeSelectDevice(
    JNIEnv *env, jclass jc,
	jint channels, jint rate) {

    WAVEFORMATEX waveFormat;
    MMRESULT result;
	int i;

	// Setup for opening a wave device
	waveFormat.wFormatTag		= WAVE_FORMAT_PCM;
	waveFormat.cbSize			= 0;
	waveFormat.wBitsPerSample	= BITSPERSAMPLE;
    waveFormat.nChannels		= (unsigned short) channels;
    waveFormat.nSamplesPerSec	= rate;
    
	waveFormat.nAvgBytesPerSec	= rate * channels * BITSPERSAMPLE / 8;
    waveFormat.nBlockAlign		= channels        * BITSPERSAMPLE / 8; 
    
    // Open the device
    result = waveOutOpen(&hDevice, WAVE_MAPPER, &waveFormat,
                        (DWORD) (HANDLE) hEvent, 0, CALLBACK_EVENT);
    
	if (result != MMSYSERR_NOERROR) {
		printErrorMessage("nativeSelectDevice", result);
        return FALSE;
    }
	// Initialize the headers
	for (i=0; i < NUMBEROFBUFFERS; i++) {
	   
		waveHdr[i].dwBufferLength = BUFFERSIZE;
		waveHdr[i].dwFlags = 0;
		waveHdr[i].dwLoops = 0;
		waveHdr[i].lpData  = pMem + (BUFFERSIZE * i);

		// Prepare the header
		waveOutPrepareHeader(hDevice, &waveHdr[i], sizeof(WAVEHDR));
	}
    // Initial buffer index
	currentBufferIndex = 0;

	return TRUE;
}

// nativePlay - Attempt to play the PCM data samples
JNIEXPORT void 
JNICALL Java_craigl_winplayer_WinPlayer_nativePlay(
    JNIEnv *env, jclass jc, jobject obj) {

	int samplesRead;

	// Get method pointer for Java callback 
	jmethodID mmid = (*env)->GetMethodID(env, jc, "requestSamples", "(I)I");

	// Request first buffer of samples
	samplesRead = (*env)->CallIntMethod(env, obj, mmid, BUFFERSIZEINSAMPLES);
	
	// While not EOF
	while (samplesRead != -1) {
		
		// Wait for buffer to become available
		WaitForSingleObject(hEvent, INFINITE);

		// Request next buffer of samples
		samplesRead = (*env)->CallIntMethod(env, obj, mmid, BUFFERSIZEINSAMPLES);
	}
}

// nativeReset - reset the wave device
JNIEXPORT void
JNICALL Java_craigl_winplayer_WinPlayer_nativeReset(
	JNIEnv *env, jclass jc) {

	waveOutReset(hDevice);
}

// nativeStopPlay - stop and close the wave device
JNIEXPORT void
JNICALL Java_craigl_winplayer_WinPlayer_nativeClose(
	JNIEnv *env, jclass jc) {

	waveOutClose(hDevice);
}

// nativeStoreSamples - stores Java samples into a wave buffer for playback
JNIEXPORT void 
JNICALL Java_craigl_winplayer_WinPlayer_nativeStoreSamples(
	JNIEnv *env, jclass jc, jshortArray buffer, jint length) {
	
	static AudioSample *pSamples = NULL;
	WAVEHDR *pWaveHdr;

	// No more data to process ?
	if (length == -1) {
		// Release the array
		(*env)->ReleaseShortArrayElements(env, buffer, pSamples, 0);
		pSamples = NULL;
		return;
	}
	
	// pSamples points at the Java samples
	pSamples = (AudioSample *)(*env)->GetShortArrayElements(env, buffer, 0);
		
	// pWaveHdr points at the buffer to use
	pWaveHdr = &waveHdr[currentBufferIndex];

	// Set amount of data to process
	pWaveHdr->dwBufferLength = length * 2;

	// Copy the samples from array into wave buffer
	memcpy((byte *) pWaveHdr->lpData, (byte *) pSamples, length * 2);

	// Write the wave data to the output device
	waveOutWrite(hDevice, pWaveHdr, sizeof(WAVEHDR));

	// Update buffer index
	currentBufferIndex = (currentBufferIndex + 1) % NUMBEROFBUFFERS;
}

// Error message routine
void printErrorMessage(char *msg, MMRESULT status) {
	char buffer[200];

	// See if an error occurred
	if (status != MMSYSERR_NOERROR) {
		// Had a problem with device
		if (status == MMSYSERR_ALLOCATED) {
			strcpy(buffer,
				"Another application is using the audio device. Stop "
                "other application and try again.");
		}	else	{

            waveOutGetErrorText(status, buffer, sizeof(buffer));
		}
        printf("%s error - %s\n", msg, buffer);
	}
}

// Main DLL entry point
BOOL WINAPI DllMain (HANDLE hInst, ULONG ul_reason_for_call,
                     LPVOID lpReserved) {
	int i;
	BOOL bResult = TRUE;
    
    // Determine why the DLL entry point was called
	switch (ul_reason_for_call) { 
	
		// 	Only do initialization on process attach
		case DLL_PROCESS_ATTACH:
			// Create an event for signalling
            // Event will be auto reset and initial state will be non-signalled
            hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
            if (hEvent == NULL) {
                printf("Error in CreateEvent\n");
                // Cannot continue
                return FALSE;
            }
			// Allocate a block of memory large enough for all sample buffers
			hMem = GlobalAlloc(GMEM_MOVEABLE, 
							   NUMBEROFBUFFERS * BUFFERSIZE);
			if (hMem == NULL) {
                printf("Unable to allocate memory for buffers\n");
				return FALSE;
			}
			pMem = (LPBYTE) GlobalLock(hMem);
            break;

		case DLL_PROCESS_DETACH:
			// Unprepare buffers
			for(i=0; i < NUMBEROFBUFFERS; i++)
				waveOutUnprepareHeader(hDevice, &waveHdr[i], sizeof(WAVEHDR));

			// Free memory although this is probably not necessary
			GlobalUnlock(hMem);
			GlobalFree(hMem);
			break;
		
        case DLL_THREAD_ATTACH:
			break;
		
        case DLL_THREAD_DETACH:
			break;
	}
	// Indicate success indication
	return bResult;
}

