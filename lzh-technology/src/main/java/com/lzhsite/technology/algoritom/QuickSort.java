package com.lzhsite.technology.algoritom;

/*所谓的快速排序的思想就是，首先把数组的第一个数拿出来做为一个key，
在前后分别设置一个i,j做为标识，然后拿这个key对这个数组从后面往前遍历，
及j--，直到找到第一个小于这个key的那个数，然后交换这两个值，交换完成后，
我们拿着这个key要从i往后遍历了，及i++;直到找到第一个大于这个key的那个数，然后交换这两个值,一直循环到i=j结束，
当这里结束后，我们会发现大于这个key的值都会跑到这个key的后面，不是的话就可能你写错了，
小于这个key的就会跑到这个值的前面；然后我们对这个分段的数组再时行递归调用就可以完成整个数组的排序。
时间复杂度nlg(n)
在最坏的情况下，待排序的序列为正序或者逆序最终其时间复杂度为O(n2)。
*/
public class QuickSort {
    public void quick_sort(int[] arrays, int lenght) {
        if (null == arrays || lenght < 1) {
            System.out.println("input error!");
            return;
        }
        _quick_sort(arrays, 0, lenght - 1);
    }

    public void _quick_sort(int[] arrays, int start, int end) {
        if(start>=end){
            return;
        }
        
        int i = start;
        int j = end;
        int value = arrays[i];
        boolean flag = true;
        while (i != j) {
            if (flag) {
                if (value > arrays[j]) {
                    swap(arrays, i, j);
                    flag=false;

                } else {
                    j--;
                }
            }else{
                if(value<arrays[i]){
                    swap(arrays, i, j);
                    flag=true;
                }else{
                    i++;
                }
            }
        }
        snp(arrays);
        _quick_sort(arrays, start, j-1);
        _quick_sort(arrays, i+1, end);
        
    }

    public void snp(int[] arrays) {
        for (int i = 0; i < arrays.length; i++) {
            System.out.print(arrays[i] + " ");
        }
        System.out.println();
    }

    private void swap(int[] arrays, int i, int j) {
        int temp;
        temp = arrays[i];
        arrays[i] = arrays[j];
        arrays[j] = temp;
    }

    public static void main(String args[]) {
    	QuickSort q = new QuickSort();
        int[] a = { 49, 38, 65,12,45,5 };
        q.quick_sort(a,6);
    } 

}