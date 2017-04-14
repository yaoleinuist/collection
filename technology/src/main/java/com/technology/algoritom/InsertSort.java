package com.technology.algoritom;
/*
   插入排序就是把当前待排序的元素插入到一个已经排好序的列表里面。 一个非常形象的例子就是右手抓取一张扑克牌，
  并把它插入左手拿着的排好序的扑克里面。插入排序的最坏运行时间是O(n2)， 
  所以并不是最优的排序算法。特点是简单，不需要额外的存储空间，在元素少的时候工作得好。*/

public class InsertSort {
	 public static void insertSort(int[] array) {  
	        if (array == null || array.length < 2) {  
	            return;  
	        }  
	  
	        for (int i = 1; i < array.length; i++) {  
	            int currentValue = array[i];  
	            int position = i;  
	            for (int j = i - 1; j >= 0; j--) {  
	                if (array[j] > currentValue) {  
	                    array[j + 1] = array[j];  
	                    position -= 1;  
	                } else {  
	                    break;  
	                }  
	            }  
	  
	            array[position] = currentValue;  
	        }  
	    }  
	  

	 
	    public static void main(String[] args) {  
	        int[] array = { 3, -1, 0, -8, 2, 1 };  
	        ArrayUtils.printArray(array);  
	        insertSort(array);  
	        ArrayUtils.printArray(array);  
	    }  
}

class ArrayUtils {  
    
    public static void printArray(int[] array) {  
        System.out.print("{");  
        for (int i = 0; i < array.length; i++) {  
            System.out.print(array[i]);  
            if (i < array.length - 1) {  
                System.out.print(", ");  
            }  
        }  
        System.out.println("}");  
    }  
}  
