package BLL;

public interface BLL_LANAudioChat {
	void StartSocketAndInitAudio();
	void StartRecordingAndSending();
	void StopRecordingAndSending();
	void StartReceivingAndSpeaking();
	void StopReceivingAndSpeaking();
}
