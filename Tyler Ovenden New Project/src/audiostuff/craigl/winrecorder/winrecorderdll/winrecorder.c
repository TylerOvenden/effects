// Win Recorder DLL
// Written by: Craig A. Lindley
// Last Update: 01/10/99
// Java native method interface DLL to Win32 wave recorder

#include <stdio.h>
#include <windows.h>
#include "..\craigl_winrecorder_WinRecorder.h"
#include <mmreg.h>

//#define DEBUG
typedef short AudioSample;

#define BUFFERSIZEINSAMPLES 7500	// Must be less than AudioConstants.SAMPLEBUFFERSIZE
#define NUMBEROFBUFFERS  6
#define BITSPERSAMPLE	16
#define BUFFERSIZE (BUFFERSIZEINSAMPLES * sizeof(AudioSample))

// Globals in this DLL
static HANDLE hInstance;            // Instance handle for this DLL
static WAVEHDR waveHeaders[NUMBEROFBUFFERS];
static HANDLE hWaveDevice;
static HANDLE hEvent;			// Event used to signal audio data ready
static BOOL bStarted;
static BOOL bInitialized;
static BOOL bEof;
static HGLOBAL hMem;
static LPBYTE pMem;
static int currentBufferIndex;

// Forward reference
void printErrorMessage(char *msg, MMRESULT status);

// Determine if this computer has a sound card capable of recording
JNIEXPORT jboolean 
JNICALL Java_craigl_winrecorder_WinRecorder_nativeHasSoundCard(
		JNIEnv *env, jobject o) {

	WAVEINCAPS waveInCaps;
	BOOL success;

	if (waveInGetNumDevs() == 0)
		return FALSE;

	// We have a recording device, grab its specs
	success = (waveInGetDevCaps(0, &waveInCaps, sizeof(WAVEINCAPS)) == MMSYSERR_NOERROR);

	if (success) {
#ifdef DEBUG
		// Display info
		printf("Manufacturer ID: %d\n", waveInCaps.wMid);
		printf("Product ID: %d\n", waveInCaps.wPid);
		printf("Maj Version: %d\n", waveInCaps.vDriverVersion >> 8);
		printf("Min Version: %d\n", waveInCaps.vDriverVersion & 255);
		printf("Product Name: %s\n", waveInCaps.szPname);
		printf("Formats: %x\n", waveInCaps.dwFormats);
		printf("Channels: %d\n", waveInCaps.wChannels);
#endif
		return TRUE;
	}	else
		return FALSE;
}

// Initialize the WAVEFORMATEX structure passed in
void initializeWaveStructure(LPWAVEFORMATEX pWaveFormEx,
							 int sampleRate, 
							 int numberOfChannels) {
	// Fill in the structure
	pWaveFormEx->wFormatTag = WAVE_FORMAT_PCM;
	pWaveFormEx->nChannels = numberOfChannels;
	pWaveFormEx->nSamplesPerSec = sampleRate;
	pWaveFormEx->nAvgBytesPerSec = 
		sampleRate * numberOfChannels * BITSPERSAMPLE / 8;
	pWaveFormEx->nBlockAlign = numberOfChannels * BITSPERSAMPLE / 8;
	pWaveFormEx->wBitsPerSample = BITSPERSAMPLE;
	pWaveFormEx->cbSize = 0;
}

// Determine if recorder is capable of this format
BOOL isSupported(int sampleRate, int numberOfChannels, int deviceID) {

	WAVEFORMATEX wfex;
	MMRESULT result;

	initializeWaveStructure(&wfex, sampleRate, numberOfChannels);

	result = waveInOpen(0, deviceID, &wfex, 0, 0, WAVE_FORMAT_QUERY);
	return result == MMSYSERR_NOERROR;
}

// Initialize the recorder as specified
JNIEXPORT jboolean 
JNICALL Java_craigl_winrecorder_WinRecorder_nativeInitRecorder(
		JNIEnv *env, jobject o, 
		jint sampleRate, jint numberOfChannels, jint deviceID) {

	WAVEFORMATEX wfex;
	MMRESULT status;
	int i;

	// Initialize values
	bStarted = FALSE;
	bInitialized = FALSE;
	bEof = FALSE;
	currentBufferIndex = 0;

	// Reset event to not signalled
	ResetEvent(hEvent);
	
	// See if format is supported
	if (!isSupported(sampleRate, numberOfChannels, deviceID)) {

		printf("WinRecorder - format not supported\n");
        return FALSE;
    }
	// Format is ok, now open the device
	initializeWaveStructure(&wfex, sampleRate, numberOfChannels);

	// Open the audio device
    status = waveInOpen(&hWaveDevice, deviceID, &wfex,
					   (DWORD) (HANDLE) hEvent,
					   0, // callback instance data
					   CALLBACK_EVENT);

	if (status != MMSYSERR_NOERROR) {
		printErrorMessage("nativeInitRecorder", status);
		return FALSE;
	}
	
	// Initialize all buffers to be used
    for (i=0; i < NUMBEROFBUFFERS; i++) {

        waveHeaders[i].dwBufferLength = BUFFERSIZE;
        waveHeaders[i].dwFlags = 0;
        waveHeaders[i].dwLoops = 0;
		waveHeaders[i].lpData = pMem + (i * BUFFERSIZE);

        waveInPrepareHeader(hWaveDevice, &waveHeaders[i], sizeof(WAVEHDR));
		waveInAddBuffer(hWaveDevice, &waveHeaders[i], sizeof(WAVEHDR));
    }
	// Signal initialization complete
	bInitialized = TRUE;
    return TRUE;
}

// Reset the wave in device
JNIEXPORT void 
JNICALL Java_craigl_winrecorder_WinRecorder_nativeResetRecorder(
		JNIEnv *env, jobject o) {

	MMRESULT status;
	int i;

	// Reset marks all buffers as done
	status = waveInReset(hWaveDevice);
	printErrorMessage("nativeResetRecorder", status);
	bEof = TRUE;
	
    // Unprepare all buffers
	for (i=0; i < NUMBEROFBUFFERS; i++)
        waveInUnprepareHeader(hWaveDevice, &waveHeaders[i], sizeof(WAVEHDR));
}

// Close the wave in device
JNIEXPORT jboolean 
JNICALL Java_craigl_winrecorder_WinRecorder_nativeCloseRecorder(
		JNIEnv *env, jobject o) {

	MMRESULT status;

	status = waveInClose(hWaveDevice);
	printErrorMessage("nativeCloseRecorder", status);
	bInitialized = FALSE;

	return (status == MMSYSERR_NOERROR);
}

// Get the PCM samples for playing
JNIEXPORT jint
JNICALL Java_craigl_winrecorder_WinRecorder_nativeGetSamples(
		JNIEnv *env, jobject o, jshortArray sampleBuffer,
		jint offset) {

	short *pSampleBuffer;
	short *pSampleShorts;
	byte *pSampleBytes;
	DWORD bytesAvailable;
	int samplesAvailable;
	int i;
	MMRESULT status;

	if (bEof)
		return -1;

	// See if acquisition has been started. Start if not
	if (!bStarted) {
		if (!bInitialized) {
			printf("Recorder not initialized\n");
			return -1;
		}
		// Reset event to not signalled
		ResetEvent(hEvent);

		// Start the acquisition
	    status = waveInStart(hWaveDevice);
		printErrorMessage("nativeGetSamples", status);
		bStarted = TRUE;
	}
	// Wait for buffer to become available
    WaitForSingleObject(hEvent, INFINITE);

	// We have a buffer to extract data from
	if ((waveHeaders[currentBufferIndex].dwFlags & WHDR_DONE) == 0) {
		printf("Buffer not done\n");
		return -1;
	}
	// Buffer is done, unprepare it
    waveInUnprepareHeader(hWaveDevice, &waveHeaders[currentBufferIndex], sizeof(WAVEHDR));

	bytesAvailable = waveHeaders[currentBufferIndex].dwBytesRecorded;

    // Get pointers to arrays
	pSampleBuffer = (short *)(*env)->GetShortArrayElements(env, sampleBuffer, 0);
	pSampleBytes  = pMem + (currentBufferIndex * BUFFERSIZE);
	pSampleShorts = (short *) pSampleBytes;
	
	// Convert bytes to 16 bit word for storage
	samplesAvailable = bytesAvailable / 2;

	for (i=0; i < samplesAvailable; i++)
		pSampleBuffer[i + offset] = pSampleShorts[i];

	// Copy the array back
	(*env)->SetShortArrayRegion(env, sampleBuffer, 0, samplesAvailable, pSampleBuffer); 
	
	// Free up the array	
	(*env)->ReleaseShortArrayElements(env, sampleBuffer, pSampleBuffer, 0);

	// Prepare buffer for resubmission
	waveHeaders[currentBufferIndex].dwBufferLength = BUFFERSIZE;
	waveHeaders[currentBufferIndex].dwFlags = 0;
	waveHeaders[currentBufferIndex].dwLoops = 0;
	waveHeaders[currentBufferIndex].lpData = pMem + (currentBufferIndex * BUFFERSIZE);

	waveInPrepareHeader(hWaveDevice, &waveHeaders[currentBufferIndex], sizeof(WAVEHDR));
	waveInAddBuffer(hWaveDevice, &waveHeaders[currentBufferIndex], sizeof(WAVEHDR));

	// Update index
	currentBufferIndex = (currentBufferIndex + 1) % NUMBEROFBUFFERS;

	return samplesAvailable;
}

void printErrorMessage(char *msg, MMRESULT status) {
	char buffer[200];

	// See if an error occurred
	if (status != MMSYSERR_NOERROR) {
		// Had a problem opening device
		if (status == MMSYSERR_ALLOCATED) {
			strcpy(buffer,
				"Another application is recording audio. Stop "
                "other application and try again.");
		}	else	{

            waveInGetErrorText(status, buffer, sizeof(buffer));
		}
        printf("%s error - %s\n", msg, buffer);
	}
}

// Main DLL entry point
BOOL WINAPI DllMain (HANDLE hInst, ULONG ul_reason_for_call,
                     LPVOID lpReserved) {
	BOOL bResult = TRUE;
    
    // Determine why the DLL entry point was called
	switch (ul_reason_for_call) { 
	
		// 	Only do initialization on process attach
		case DLL_PROCESS_ATTACH:
			//printf("DLL_PROCESS_ATTACH\n");
			// Save instance identifier
			hInstance = hInst;
	
			// Create an event for signalling
            // Event will be auto reset and initial state will be non-signalled
            hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
            if (hEvent == NULL) {
                printf("Error in CreateEvent\n");
                // Cannot continue
                return FALSE;
            }
			// Allocate a block of memory large enough for all sample buffers
			hMem = GlobalAlloc(GMEM_MOVEABLE, NUMBEROFBUFFERS * BUFFERSIZE);
			if (hMem == NULL) {
                printf("Unable to allocate memory for buffers\n");
				return FALSE;
			}
			pMem = (LPBYTE) GlobalLock(hMem);
			break;

		case DLL_PROCESS_DETACH:
            //printf("DLL_PROCESS_DETACH\n");

			// Destroy event
            CloseHandle(hEvent);

			// Free memory although this is probably not necessary
			GlobalUnlock(hMem);
			GlobalFree(hMem);
			break;
		
        case DLL_THREAD_ATTACH:
            //printf("DLL_THREAD_DETACH\n");
			break;
		
        case DLL_THREAD_DETACH:
            //printf("DLL_THREAD_DETACH\n");
			break;
	}
	// Indicate success indication
	return bResult;
}

