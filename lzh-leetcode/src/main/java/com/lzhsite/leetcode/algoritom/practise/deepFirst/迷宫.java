package com.lzhsite.leetcode.algoritom.practise.deepFirst;


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
*/
public class 迷宫 {
 

	char migon[][] = { { 'S', '.', 'X', '.' }, { '.', '.', 'X', '.' }, { '.', '.', 'X', 'D' }, { '.', '.', '.', '.' } };

	int n= migon.length;
	int m= migon[0].length;
	
	//起点横纵坐标
	int tagret_x = 0, target_y = 0;

	int [][] res =new int[n][m];
	boolean [][] visits =new boolean[n][m];
	
	/**
	 * 
	 * @param depth 从终点已经走出去的长度
	 * @param x 当前横标坐
	 * @param y 当前纵标坐
	 * @param res 
	 * @return  从终点走到(x,y)的最短路径
	 */
	private int dfs(int depth, int x, int y,int [][] res) {
		// TODO Auto-generated method stub
        if(res[x][y]!=0){
        	return res[x][y];
        }
		
		
		if (x == tagret_x && y == target_y) {
			//System.out.println(depth);
			return depth;
		}

		int min=100000;
		//可以从四个方向走到 x,y 取其中深度最小的个方向即可
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// 保证是上下左右四个方向
				if (Math.abs(i + j) == 1) { 
					// 检查坐标有效性
					if (x + i > -1 && x + i < n && y + j > -1 && y + j < m) {
						
						if(visits[x][y]==false){
							if(migon[x+i][y+j]=='.'|| migon[x+i][y+j]=='D'|| migon[x+i][y+j]=='S'){
								visits[x][y]=true;
								//不需要加1因为depth已经进行了累加
								min=Math.min(min,dfs(depth +1, x +i, y+j,res));
								visits[x][y]=false;
							}
						}

					}
				}
			}
		}


		res[x][y]=min;
		return min;
	}
	
	public static void main(String[] args) {
		
		迷宫  a =  new 迷宫();
		System.out.println(a.dfs(0,2,3,a.res));
	}

}
