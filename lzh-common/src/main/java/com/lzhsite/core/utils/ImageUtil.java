package com.lzhsite.core.utils;

//import com.sun.image.codec.jpeg.ImageFormatException;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.*;
import java.net.URL;


@SuppressWarnings("restriction")
public class ImageUtil {
	
	private final static ImageUtil iu = new ImageUtil();
	
    private Font font = new Font("华文彩云", Font.PLAIN, 40);// 添加字体的属性设置  
    
    private Graphics2D g = null;  
  
    private int fontsize = 0;  
  
    private int x = 0;  
  
    private int y = 0;  
 

	public static ImageUtil getInstance() {
		return iu;
	}
	/**
	 * 切割图片
	 * @param os 切割后的输出流
	 * @param is 输入流
	 * @param type 文件的图片类型
	 * @param x x坐标
	 * @param y y坐标
	 * @param width 宽度
	 * @param height 高度
	 */
	public void cropImg(OutputStream os,InputStream is,String type,int x,int y,int width,int height) {
		Image img = null;
		BufferedImage imgBuf = null;
		ImageFilter cropFilter = null;
		try {
			imgBuf = ImageIO.read(is);
			cropFilter = new CropImageFilter(x,y,width,height);
			img = Toolkit.getDefaultToolkit().
					createImage(new FilteredImageSource(imgBuf.getSource(), cropFilter));
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(img, 0, 0, null);
			ImageIO.write(tag, type, os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(os!=null) os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
/**
 * 切割图片
 * @param is 输入流
 * @param x x坐标
 * @param y y坐标
 * @param width 宽度
 * @param height 高度
 */
public String cropImg(String oPath,InputStream is,int x,int y,int width,int height) {
	String type = oPath.substring(oPath.lastIndexOf(".")+1);
	try {
		OutputStream os = new FileOutputStream(oPath);
		cropImg(os, is,type,x, y, width, height);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	return null;
}

/**
 * 切割图片
 * @param oPath 输出文件
 * @param iPath 输入文件名
 * @param x x坐标
 * @param y y坐标
 * @param width 宽度
 * @param height 高度
 */
public String cropImg(String oPath,String iPath,int x,int y,int width,int height) {
	String type = oPath.substring(oPath.lastIndexOf(".")+1);
	try {
		OutputStream os = new FileOutputStream(oPath);
		InputStream is = new FileInputStream(iPath);
		cropImg(os, is,type,x, y, width, height);
		return oPath;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	return null;
}

/**
 * 切割图片自动根据输入的文件名转换为xxx_small.type
 * @param iPath 输入文件名
 * @param x x坐标
 * @param y y坐标
 * @param width 宽度
 * @param height 高度
 */
public String cropImg(String iPath,int x,int y,int width,int height) {
	String type = iPath.substring(iPath.lastIndexOf(".")+1);
	String sname = generatorSmallFileName(iPath);
	try {
		InputStream is = new FileInputStream(iPath);
		OutputStream os = new FileOutputStream(sname);
		cropImg(os, is,type,x, y, width, height);
		return sname;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	return null;
}

private String generatorSmallFileName(String name) {
	String fn = name.substring(0,name.lastIndexOf("."));
	return name.replace(fn,fn+"_small");
}
	
	/**
	 * 转换图片操作
	 * @param os 要转换图片的输出流
	 * @param is 要转换图片的输入流
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @throws java.io.IOException
	 */
	public void compressImg(OutputStream os,InputStream is,int width,int height,boolean proportion ) {
		compressImg(os, is, width, height, proportion,false);
	}

	/**
	 * 转换图片操作
	 * @param os 要转换图片的输出流
	 * @param is 要转换图片的输入流
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @param magnify 是否进行放大
	 * @throws java.io.IOException
	 */
	public void compressImg(OutputStream os,InputStream is,int width,int height,boolean proportion,boolean magnify) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(is);
			System.out.println(img);
			int newWidth; int newHeight;
			int oldWidth = img.getWidth(null);
			int oldHeight = img.getHeight(null);
			boolean isWrite = false;
			if(!magnify) {
				boolean iw = width>=height?true:false;
				if(iw) {
					if(width>oldWidth) isWrite = true;
				} else {
					if(height>oldHeight) isWrite = true;
				}
//				System.out.println(width+","+height);
//				System.out.println(img.getWidth(null)+","+img.getHeight(null));
				if(isWrite) {
					System.out.println("write");
					ImageIO.write(img,"jpg", os);
					os.flush();
				}
//				System.out.println(newWidth+","+newHeight);
			}
			if(!isWrite) {
				// 判断是否是等比缩放
				if (proportion) {
					// 为等比缩放计算输出的图片宽度及高度
					double rate1 = ((double) oldWidth) / (double) width + 0.1;
					double rate2 = ((double) oldHeight) / (double) height + 0.1;
					System.out.println((rate1+","+rate2));
					// 根据缩放比率大的进行缩放控制
					double rate = rate1 < rate2 ? rate1 : rate2;
					newWidth = (int) (((double) img.getWidth(null)) / rate);
					newHeight = (int) (((double) img.getHeight(null)) / rate);
				} else {
					newWidth = width; // 输出的图片宽度
					newHeight = height; // 输出的图片高度
				}
				BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);
				tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
//				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
//				encoder.encode(tag);
				ImageIO.write(tag,"jpg",os); // modify by reese
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(os!=null) os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void saveImage(BufferedImage dstImage, String dstName) throws IOException {
		String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
		//FileOutputStream out = new FileOutputStream(dstName);
		//JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		//encoder.encode(dstImage);
		ImageIO.write(dstImage, /*"GIF"*/ formatName /* format desired */ , new File(dstName) /* target */ );
	}

	/**
	 * 转换图片操作
	 * @param outputFile 输出文件
	 * @param inputFile 输入文件
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @throws java.io.IOException
	 */
	public String compressImg(File outputFile, File inputFile,int width,int height,boolean proportion ) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(inputFile);
			os = new FileOutputStream(outputFile);
			compressImg(os, is, width, height, proportion);
			return outputFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转换图片操作
	 * @param outputFile 输出文件
	 * @param is 输入流
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @throws java.io.IOException
	 */
	public String compressImg(File outputFile, InputStream is,int width,int height,boolean proportion ) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(outputFile);
			compressImg(os, is, width, height, proportion);
			return outputFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转换图片操作
	 * @param oPath 输出路径
	 * @param is 输入流
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @throws java.io.IOException
	 */
	public String compressImg(String oPath, InputStream is,int width,int height,boolean proportion ) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(oPath);
			compressImg(os, is, width, height, proportion);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return oPath;
	}

	/**
	 * 转换图片操作
	 * @param oPath 输出路径
	 * @param iPath 输入路径
	 * @param width 要压缩的宽度
	 * @param height 要压缩的高度
	 * @param proportion 是否进行等比例压缩
	 * @throws java.io.IOException
	 */
	public String compressImg(String oPath, String iPath,int width,int height,boolean proportion ) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(iPath);
			os = new FileOutputStream(oPath);
			compressImg(os, is, width, height, proportion);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return oPath;
	}



	public int getWidth(InputStream is) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(is);
		return img.getWidth(null);
	}

	public int getWidth(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			return getWidth(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getWidth(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return getWidth(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getHeight(InputStream is) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(is);
		return img.getHeight(null);
	}

	public int getHeight(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			return getHeight(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getHeight(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return getHeight(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 返回一个数组，第一个值是宽，第二个值是高
	 * @param is
	 * @return 返回一个数组，第一个值是宽，第二个值是高
	 * @throws java.io.IOException
	 */
	public int[] getWidthAndHeight(InputStream is) throws IOException {
		Image img = null;
		img = ImageIO.read(is);
		return new int[]{img.getWidth(null),img.getHeight(null)};
	}
	/**
	 * 返回一个数组，第一个值是宽，第二个值是高
	 * @return 返回一个数组，第一个值是宽，第二个值是高
	 * @throws java.io.IOException
	 */
	public int[] getWidthAndHeight(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			return getWidthAndHeight(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 返回一个数组，第一个值是宽，第二个值是高
	 * @return 返回一个数组，第一个值是宽，第二个值是高
	 * @throws java.io.IOException
	 */
	public int[] getWidthAndHeight(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return getWidthAndHeight(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

  
    /** 
     * 导入本地图片到缓冲区 
     */  
    public BufferedImage loadImageLocal(String imgName) {  
        try {  
            return ImageIO.read(new File(imgName));  
        } catch (IOException e) {  
            System.out.println(e.getMessage());  
        }  
        return null;  
    }  
  
    /** 
     * 导入网络图片到缓冲区 
     */  
    public BufferedImage loadImageUrl(String imgName) {  
        try {  
            URL url = new URL(imgName);  
            return ImageIO.read(url);  
        } catch (IOException e) {  
            System.out.println(e.getMessage());  
        }  
        return null;  
    }  
  
    /** 
     * 生成新图片到本地 
     */  
    public void writeImageLocal(String newImage, BufferedImage img) {  
        if (newImage != null && img != null) {  
            try {  
                File outputfile = new File(newImage);  
                ImageIO.write(img, "jpg", outputfile);  
            } catch (IOException e) {  
                System.out.println(e.getMessage());  
            }  
        }  
    }  
  
    /** 
     * 设定文字的字体等 
     */  
    public void setFont(String fontStyle, int fontSize) {  
        this.fontsize = fontSize;  
        this.font = new Font(fontStyle, Font.PLAIN, fontSize);  
    }  
  
    /** 
     * 修改图片,返回修改后的图片缓冲区（只输出一行文本） 
     */  
    public BufferedImage modifyImage(BufferedImage img, Object content, int x,  
            int y) {  
  
        try {  
            int w = img.getWidth();  
            int h = img.getHeight();  
            g = img.createGraphics();  
            g.setBackground(Color.WHITE);  
            g.setColor(Color.orange);//设置字体颜色  
            if (this.font != null)  
                g.setFont(this.font);  
            // 验证输出位置的纵坐标和横坐标  
            if (x >= h || y >= w) {  
                this.x = h - this.fontsize + 2;  
                this.y = w;  
            } else {  
                this.x = x;  
                this.y = y;  
            }  
            if (content != null) {  
                g.drawString(content.toString(), this.x, this.y);  
            }  
            g.dispose();  
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }  
  
        return img;  
    }  
  
    /** 
     * 修改图片,返回修改后的图片缓冲区（输出多个文本段） xory：true表示将内容在一行中输出；false表示将内容多行输出 
     */  
    public BufferedImage modifyImage(BufferedImage img, Object[] contentArr,  
            int x, int y, boolean xory) {  
        try {  
            int w = img.getWidth();  
            int h = img.getHeight();  
            g = img.createGraphics();  
            g.setBackground(Color.WHITE);  
            g.setColor(Color.RED);  
            if (this.font != null)  
                g.setFont(this.font);  
            // 验证输出位置的纵坐标和横坐标  
            if (x >= h || y >= w) {  
                this.x = h - this.fontsize + 2;  
                this.y = w;  
            } else {  
                this.x = x;  
                this.y = y;  
            }  
            if (contentArr != null) {  
                int arrlen = contentArr.length;  
                if (xory) {  
                    for (int i = 0; i < arrlen; i++) {  
                        g.drawString(contentArr[i].toString(), this.x, this.y);  
                        this.x += contentArr[i].toString().length()  
                                * this.fontsize / 2 + 5;// 重新计算文本输出位置  
                    }  
                } else {  
                    for (int i = 0; i < arrlen; i++) {  
                        g.drawString(contentArr[i].toString(), this.x, this.y);  
                        this.y += this.fontsize + 2;// 重新计算文本输出位置  
                    }  
                }  
            }  
            g.dispose();  
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }  
  
        return img;  
    }  
  
    /** 
     * 修改图片,返回修改后的图片缓冲区（只输出一行文本） 
     *  
     * 时间:2007-10-8 
     *  
     * @param img 
     * @return 
     */  
    public BufferedImage modifyImageYe(BufferedImage img) {  
  
        try {  
            int w = img.getWidth();  
            int h = img.getHeight();  
            g = img.createGraphics();  
            g.setBackground(Color.WHITE);  
            g.setColor(Color.blue);//设置字体颜色  
            if (this.font != null)  
                g.setFont(this.font);  
            g.drawString("reyo.cn", w - 85, h - 5);  
            g.dispose();  
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }  
  
        return img;  
    }  
  
    public BufferedImage modifyImagetogeter(BufferedImage b, BufferedImage d) {  
  
        try {  
            int w = b.getWidth();  
            int h = b.getHeight();  
              
            g = d.createGraphics();  
            g.drawImage(b, 100, 10, w, h, null);  
            g.dispose();  
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }  
  
        return d;  
    }  
  
}
