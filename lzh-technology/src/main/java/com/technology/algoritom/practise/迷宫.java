package com.technology.algoritom.practise;
/*
 小希的女朋友被绑架了，他要救女朋友就必须通过一个迷宫，但是这个迷宫很诡异，
这个迷宫是由二维的方砖组成的矩阵，每个方砖上都标记有一个字符，
总共有四种格式的字符，’S’  ，’ . ‘ ，’X’ ，’D’ ，标记’X’ 的代表这块砖上是一个柱子，
所以这块砖，不能站人。标记为 ‘ . ‘ 表示这块砖可以走人。标记为 ‘D’的表示这是终点，标记为’S’的代表这是入口。
只有在T秒时迷宫的出口的门才会打开，他站立的方砖会在一秒后破碎（如果破碎时他没有逃走，那他就狗带了），
小希移动一个位置的时间也是一秒，意味着他必须动，而且不可能在同一块方砖上站两次。请你估算一下，
小希能不能在T时间时逃出迷宫。如果可以输出YES，不可以输出NO。

输入样例：
 
4 4 5 （第一个和第二个代表这这个矩阵的行数和列数，第三个数代表着T秒，迷宫的出口在此时打开）
S.X.
..X.
..XD
....
输出样例：
NO

输入样例：
3 4 5
S.X.
..X.
...D
输出样例：
YES

【解题提示】：

    请在评论中先给出你的实现思路；
    然后贴出实现的代码；
    编程语言不限；
    评论支持代码高亮，请点击评论框菜单栏上的 <> 按钮；

*/
public class 迷宫 {

	
	int migon[][]={{1,1,0,1,1},
					{1,1,0,1},
					{1,1,1,1},
					{1,1,1,1}};
	
	
	
	private void dfs(int depth,int x,int y,int [][]migonTemp) {
		// TODO Auto-generated method stub
		
		if((migonTemp[x-1][y]==0 &&migonTemp[x-1][y]==0&&migonTemp[x-1][y]==0&&migonTemp[x-1][y]==0)
				||(x==3&&y==2)){
			System.out.println(depth);
			return;
		}
		
       if(migonTemp[x-1][y]==1){
    	   migonTemp[x-1][y]=0;
    	   dfs(depth-1,x-1,y,migonTemp);
       }
       if(migonTemp[x+1][y]==1){
    	   migonTemp[x+1][y]=0;
    	   dfs(depth+1,x+1,y,migonTemp);
       }
       if(migonTemp[x][y-1]==1){
    	   migonTemp[x-1][y]=0;
    	   dfs(depth-1,x,y-1,migonTemp);
       }
       if(migonTemp[x][y+1]==1){
    	   migonTemp[x][y+1]=0;
    	   dfs(depth,x,y+1,migonTemp);
       }
	}
	
}
