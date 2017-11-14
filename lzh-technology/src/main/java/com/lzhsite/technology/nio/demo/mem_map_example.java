package com.lzhsite.technology.nio.demo;

import java.io.RandomAccessFile;

import java.nio.MappedByteBuffer;

import java.nio.channels.FileChannel;
/**
 * 这个简单的示例创建了一个20M的文件example_memory_mapped_file.txt，并且用字符A对它进行填充，
 * 然后读取前30个字节。在实际的应用中，内存映射不仅仅擅长提高I/O的原始速度，
 * 同时它也允许多个不同的reader和writer同时处理同一个文件镜像。这个技术功能强大但是也很危险，
 * 不过如果正确使用的话，它会使得你的IO速度提高数倍。
 * 众所周知，华尔街的交易操作为了能够赢得秒级甚至是毫秒级的优势，都使用了内存映射技术。
 * @author lzh
 *
 */


  public class mem_map_example {

    private static int mem_map_size = 20 * 1024 * 1024;

    private static String fn = "example_memory_mapped_file.txt";



    public static void main(String[] args) throws Exception {

        RandomAccessFile memoryMappedFile = new RandomAccessFile(fn, "rw");



        //Mapping a file into memory

        MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, mem_map_size);



        //Writing into Memory Mapped File

        for (int i = 0; i < mem_map_size; i++) {

            out.put((byte) 'A');

        }

        System.out.println("File '" + fn + "' is now " + Integer.toString(mem_map_size) + " bytes full.");



        // Read from memory-mapped file.

        for (int i = 0; i < 30 ; i++) {

            System.out.print((char) out.get(i));

        }

        System.out.println("\nReading from memory-mapped file '" + fn + "' is complete.");

    }

}


