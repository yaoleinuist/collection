package com.technology.nio.demo;
//Java NIO浅析
//http://tech.meituan.com/nio.html?utm_source=tuicool&utm_medium=referral
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
/**
 * 我在这篇文章里要讲解的NIO的最后一个特性是charset，一个用来转换不同字符编码的包。在NIO之前，
 * Java通过getByte方法内置实现了大部分相同的功能。charset很受欢迎，因为它比getBytes更加灵活，
 * 并且能够在更底层去实现，这样就能够获得更好的性能。这个对于搜索那些对于编码、顺序以及其他语言特点
 * 比较敏感的非英语语言而言更加有价值。
 * 列表4展示了一个把Java里的Unicode字符转换成Latin-1的示例
 * @author lzh
 *
 */
public final class CharConvrt {
	
	public static void main(String[] args) {
	
	    String some_string = "This is a string that Java natively stores as Unicode.";
	
	     Charset latin1_charset = Charset.forName("ISO-8859-1");
	
	     CharsetEncoder latin1_encoder = latin1_charset.newEncoder();
	
	     try {
			ByteBuffer latin1_bbuf = latin1_encoder.encode(CharBuffer.wrap(some_string));
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
