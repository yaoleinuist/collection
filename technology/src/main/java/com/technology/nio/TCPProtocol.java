package com.technology.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface TCPProtocol {
	
	void handleAccept(SelectionKey key) throws IOException;
	void handleRead(SelectionKey key) throws IOException;
	void handleWrite(SelectionKey key) throws IOException;
}
