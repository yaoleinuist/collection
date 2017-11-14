package com.lzhsite.technology.algoritom;

public class SelectSort {
    //选择排序  每一趟从待排序的数据元素中选出最小（或最大）的一个元素，顺序放在已排好序的数列的最后，直到全部待排序的数据元素排完。 
    public void selectSort(int [] a) {  
        int n = a.length;  
        for(int k=0; k<n-1; k++) {  
            int min = k;  
            for(int i=k+1; i<n; i++) {  
                if(a[i] < a[min]) {  
                    min = i;  
                }  
            }  
            if(k != min) {  
                int temp = a[k];  
                a[k] = a[min];  
                a[min] = temp;  
            }  
        }  
    }  
}
