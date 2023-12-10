package BLL;

public interface BLL_LANChat {
	void Start();
	void Stop();
	void sendMessage(String message);
	void sendFile(String path);
}
