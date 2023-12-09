package NET;

import java.io.IOException;

public interface LANChat {
	public void run();
	public void sendMessage(String message);
	public void sendFile(String path, String fileName);
	public void open() throws IOException;
	public void close() throws IOException;
	public void stopChat();
}
