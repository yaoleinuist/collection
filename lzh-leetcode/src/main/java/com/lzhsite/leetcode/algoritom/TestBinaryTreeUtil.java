package com.lzhsite.leetcode.algoritom;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.lzhsite.leetcode.algoritom.dataSructure.Iterator;
import com.lzhsite.leetcode.algoritom.dataSructure.tree.BinTreeNode;
import com.lzhsite.leetcode.algoritom.dataSructure.tree.BinaryTreeLinked;

public class TestBinaryTreeUtil {

	private static Scanner input = new Scanner(System.in);

	// 初始化二叉树的根
	private static BinTreeNode InitTree() {
		BinTreeNode node;
		// 申请内存
		if ((node = new BinTreeNode()) != null) {
			System.out.printf("请先输入一个根结点数据:\n");
			node.setData(input.next());
			node.setLChild(null);
			node.setRChild(null);
			return node;
		}
		return null;
	}

	// 添加结点
	private static void AddTreeNode(BinTreeNode treeNode, BinaryTreeLinked binaryTreeLinked) {
		BinTreeNode pnode, parent;
		String data;
		int menusel;
		// 分配内存
		if ((pnode = new BinTreeNode()) != null) {
			System.out.printf("输入二叉树结点数据:\n");

			pnode.setData(input.next());
			pnode.setLChild(null); // 设置左右子树为空
			pnode.setRChild(null);

			System.out.printf("输入该结点的父结点数据:");
			data = input.next();

			parent = binaryTreeLinked.find(data); // 查找指定数据的结点
			// 如果未找到
			if (parent == null) {
				System.out.printf("未找到该父结点!\n");
				pnode = null; // 释放创建的结点内存
				return;
			}
			System.out.printf("1.添加该结点到左子树\n2.添加该结点到右子树\n");
			do {
				menusel = input.nextInt(); // 输入选择项

				if (menusel == 1 || menusel == 2) {
					if (parent == null) {
						System.out.printf("不存在父结点，请先设置父结点!\n");
					} else {
						switch (menusel) {
						case 1: // 添加到左结点
							if (parent.getLChild() != null) {
								System.out.printf("左子树结点不为空!\n");
							} else {
								parent.setLChild(pnode);
							}
							break;
						case 2: // 添加到右结点
							if (parent.getRChild() != null) {
								System.out.printf("右子树结点不为空!\n");
							} else {
								parent.setRChild(pnode);
							}
							break;
						default:
							System.out.printf("无效参数!\n");
						}
					}
				}
			} while (menusel != 1 && menusel != 2);
		}
	}

	public static void printElement(Iterator iterator) {
		for (Iterator i = iterator; i.isDone();) {
			String str = (String) i.currentItem();
			i.next();
			System.out.println(str);

		}
	}

	/**
	 * @param datas  实现二叉树各节点值的数组
	 */
	public static BinTreeNode creatBinaryTree(int[] datas) {

		List<BinTreeNode> nodelist = new LinkedList<>();

		// 将数组变成node节点
		for (int nodeindex = 0; nodeindex < datas.length; nodeindex++) {
			BinTreeNode node = new BinTreeNode(datas[nodeindex]);
			nodelist.add(node);
		}
		// 给所有父节点设定子节点
		for (int index = 0; index < nodelist.size() / 2 - 1; index++) {
			// 编号为n的节点他的左子节点编号为2*n 右子节点编号为2*n+1 但是因为list从0开始编号，所以还要+1
			// 这里父节点有1（2,3）,2（4,5）,3（6,7）,4（8,9） 但是最后一个父节点有可能没有右子节点 需要单独处理
			nodelist.get(index).setLChild(nodelist.get(index * 2 + 1));
			nodelist.get(index).setRChild(nodelist.get(index * 2 + 2));
		}
		// 单独处理最后一个父节点 因为它有可能没有右子节点
		int index = nodelist.size() / 2 - 1;
		nodelist.get(index).setLChild(nodelist.get(index * 2 + 1)); // 先设置左子节点
		if (nodelist.size() % 2 == 1) { // 如果有奇数个节点，最后一个父节点才有右子节点
			nodelist.get(index).setRChild(nodelist.get(index * 2 + 2));
		}
		
		return nodelist.get(0);
	}

	public static void main(String[] args) {
		BinTreeNode root = null; // root为指向二叉树根结点的指针
		int menusel;
		// 设置根元素
		root = InitTree();
		BinaryTreeLinked binaryTreeLinked = new BinaryTreeLinked(root);

		// 添加结点
		do {
			System.out.printf("请选择菜单添加二叉树的结点\n");
			System.out.printf("0.退出\t"); // 显示菜单
			System.out.printf("1.添加二叉树的结点\n");
			menusel = input.nextInt();
			switch (menusel) {
			case 1: // 添加结点
				AddTreeNode(root, binaryTreeLinked);
				break;
			case 0:
				break;
			default:
				;
			}
		} while (menusel != 0);

		// 遍历
		do {
			System.out.printf("请选择菜单遍历二叉树,输入0表示退出:\n");
			System.out.printf("1.先序遍历DLR\t"); // 显示菜单
			System.out.printf("2.中序遍历LDR\n");
			System.out.printf("3.后序遍历LRD\t");
			System.out.printf("4.按层遍历\n");
			menusel = input.nextInt();
			switch (menusel) {
			case 0:
				break;
			case 1: // 先序遍历
				System.out.printf("\n先序遍历DLR的结果：");
				printElement(binaryTreeLinked.preOrder());
				System.out.printf("\n");
				break;
			case 2: // 中序遍历
				System.out.printf("\n中序LDR遍历的结果：");
				printElement(binaryTreeLinked.inOrder());
				System.out.printf("\n");
				break;
			case 3: // 后序遍历
				System.out.printf("\n后序遍历LRD的结果：");
				printElement(binaryTreeLinked.postOrder());
				System.out.printf("\n");
				break;
			case 4: // 按层遍历
				System.out.printf("\n按层遍历的结果：");
				printElement(binaryTreeLinked.levelOrder());
				System.out.printf("\n");
				break;
			default:
				;
			}
		} while (menusel != 0);
		// 深度
		System.out.printf("\n二叉树深度为:%d\n", binaryTreeLinked.getHeight());
		// 清空二叉树
		binaryTreeLinked.ClearTree(root);
		root = null;

	}
}
