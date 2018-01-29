package com.lzhsite.technology.algoritom.dataSructure;


public class StackApplication {
	
	public static void main(String[] args) {
		StackApplication app = new StackApplication();
		app.baseConversion(2007);
		String str = "{(1.1+2)*[(2.02-2)+(3-1)]}^[(4-1)+(6-3)]";
		System.out.println("\n"+app.bracketMatch(str));
		char[][] maze = {{'1','1','1','1','1','1','1','1','1','1'},
						 {'1','0','0','1','1','1','0','0','1','1'},
						 {'1','0','0','1','1','0','0','1','0','1'},
						 {'1','0','0','0','0','0','0','1','0','1'},
						 {'1','0','0','0','0','1','1','0','0','1'},
						 {'1','0','0','1','1','1','0','0','0','1'},
						 {'1','0','0','0','0','1','0','1','0','1'},
						 {'1','0','1','1','0','0','0','1','0','1'},
						 {'1','1','0','0','0','0','1','0','0','1'},
						 {'1','1','1','1','1','1','1','1','1','1'}};
		app.mazeExit(maze,8,8,1,7);
	}
	
	public void baseConversion(int i){
		Stack s = new StackSLinked();
		while (i>0){
			s.push(i%8+"");
			i = i/8;
		}
		while (!s.isEmpty()) System.out.print((String)s.pop());
	}
	
	public boolean bracketMatch(String str) {
		Stack s = new StackSLinked();
		for (int i=0;i<str.length();i++)
		{	
 		   	char c = str.charAt(i);
    		switch (c)
    		{
        		case '{':
        		case '[':
        		case '(': s.push(Integer.valueOf(c)); break;
        		case '}':
		            if (!s.isEmpty()&& ((Integer)s.pop()).intValue()=='{')
                		break;
            		else return false;
        		case ']':
		            if (!s.isEmpty()&& ((Integer)s.pop()).intValue()=='[')
                		break;
	            	else return false;
    	    	case ')':
	            	if (!s.isEmpty()&& ((Integer)s.pop()).intValue()=='(')
	                	break;
            		else return false;
    		}
		}
		if (s.isEmpty()) return true;
		else	return false;
	}
	
	public void mazeExit(char[][] maze,int sx,int sy, int ex,int ey){
		Cell[][] cells = createMaze(maze);	//�������Թ�
		printMaze(cells);					//��ӡ�Թ�
		Stack s = new StackSLinked();		//�����ջ
		Cell startCell = cells[sx][sy];		//���
		Cell endCell = cells[ex][ey];		//�յ�
		s.push(startCell);					//�����ջ
		startCell.visited = true;			//�������ѱ�����
		while (!s.isEmpty()){
			Cell current = (Cell)s.peek();
			if (current==endCell){	//·���ҵ�
				while(!s.isEmpty()){
					Cell cell = (Cell)s.pop();//��·���ؽ�·���ϵĵ�Ԫ��Ϊ*
					cell.c = '*';
					//��ջ����cell���ڵĵ�Ԫ����·������ɲ���
					//����֮�⣬��ջ�л��м�¼��������δ��������̽���ĵ�Ԫ����Щ��Ԫֱ�ӳ�ջ
					while(!s.isEmpty()&&!isAdjacentCell((Cell)s.peek(),cell)) s.pop();
				}
				System.out.println("�ҵ�����㵽�յ��·����");
				printMaze(cells);
				return;
			}
			else {	//�����ǰλ�ò����յ�
				int x = current.x;
				int y = current.y;
				int count = 0;
				if(isValidWayCell(cells[x+1][y])){	//����
					s.push(cells[x+1][y]); cells[x+1][y].visited = true; count++;
				}
				if(isValidWayCell(cells[x][y+1])){	//����
					s.push(cells[x][y+1]); cells[x][y+1].visited = true; count++;
				}
				if(isValidWayCell(cells[x-1][y])){	//����
					s.push(cells[x-1][y]); cells[x-1][y].visited = true; count++;
				}
				if(isValidWayCell(cells[x][y-1])){	//����
					s.push(cells[x][y-1]); cells[x][y-1].visited = true; count++;
				}
				if (count==0) s.pop();//��������㣬��ջ
			}
		}
		System.out.println("û�д���㵽�յ��·����");
	}
	
	private void printMaze(Cell[][] cells){
		for (int x=0;x<cells.length;x++){
			for (int y=0;y<cells[x].length;y++)
				System.out.print(cells[x][y].c);
			System.out.println();
		}	
	}
	
	private boolean isAdjacentCell(Cell cell1, Cell cell2){
		if (cell1.x==cell2.x&&Math.abs(cell1.y-cell2.y)<2) return true;
		if (cell1.y==cell2.y&&Math.abs(cell1.x-cell2.x)<2) return true;
		return false;
	}
	private boolean isValidWayCell(Cell cell){
		return cell.c=='0'&&!cell.visited;
	}
	
	private Cell[][] createMaze(char[][] maze){
		Cell[][] cells = new Cell[maze.length][];
		for (int x=0;x<maze.length;x++){
			char[] row = maze[x];
			cells[x] = new Cell[row.length];
			for (int y=0; y<row.length;y++)
				cells[x][y] = new Cell(x,y,maze[x][y],false);
		}
		return cells;
	}
	private class Cell{
		int x = 0;	//��Ԫ������
		int y = 0;	//��Ԫ������
		boolean visited = false;	//�Ƿ���ʹ�
		char c = ' ';				//��ǽ����ͨ·����㵽�յ��·��
		public Cell(int x, int y, char c, boolean visited){
			this.x = x;	this.y = y;	this.c = c;	this.visited = visited;
		}
	}
}
