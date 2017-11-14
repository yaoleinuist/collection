package com.lzhsite.technology.nio.demo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

/**
 * 变更通知（因为每个事件都需要一个监听者）

      很多企业级应用需要在下面的情况时做一些特殊的处理：

       当一个文件上传到一个FTP文件夹里时

       当一个配置里的定义被修改时

       当一个草稿文档被上传时

       其他的文件系统事件出现时

	这些都是变更通知或者变更响应的例子。在Java（以及其他语言）的早期版本里，
	轮询（polling）是检测这些变更事件的最好方式。轮询是一种特殊的无限循环：检查文件系统或者其他对象，
	并且和之前的状态对比，如果没有变化，在大概几百个毫秒或者10秒的间隔后，继续检查。就这一直无限循环下去。
 *  @author Administrator
 *
 */

public class Watcher {

    public static void main(String[] args) {

        Path this_dir = Paths.get(".");    

        System.out.println("Now watching the current directory ...");  



        try {

            WatchService watcher = this_dir.getFileSystem().newWatchService();

            this_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);



            WatchKey watckKey = watcher.take();



            List<WatchEvent<?>> events = watckKey.pollEvents();

            for (WatchEvent event : events) {

                System.out.println("Someone just created the file '" + event.context().toString() + "'.");



           }



       } catch (Exception e) {

           System.out.println("Error: " + e.toString());

       }

    }

}