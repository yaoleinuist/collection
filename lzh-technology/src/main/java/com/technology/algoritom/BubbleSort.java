package com.technology.algoritom;

/*  
 * 冒泡排序  
 * 如果需要将一组数，以从小到大的顺序排列，那么就可以设计这样的冒泡方法：
 * 可以设计从序列的最后面开始，找出序列中最小的一个数放到序列的最前面，这样经过n次循环也可以实现数组的排列。
 * 这种排序方法由于每一次找到的数字都像是气泡一样从数组里冒出来而得名为“冒泡排序”。 
 */  
public class BubbleSort {  
      
    public void bubble(Integer[] data){  
        for(int i=0;i<data.length;i++){  
            for(int j=0;j<data.length-1-i;j++){  
                if(data[j]>data[j+1]){   //如果后一个数小于前一个数交换  
                    int tmp=data[j];  
                    data[j]=data[j+1];  
                    data[j+1]=tmp;  
                }  
            }  
        }  
    }  
    
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
         Integer[] list={49,38,65,97,76,13,27,14,10};  
         //快速排序  
        /* QuicSort qs=new QuicSort();  
         qs.quick(list);*/  
         //冒泡排序  
         BubbleSort bs=new BubbleSort();  
         bs.bubble(list);  
           
         for(int i=0;i<list.length;i++){  
             System.out.print(list[i]+" ");  
         }  
         System.out.println();  
    }  
}  