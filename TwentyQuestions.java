/*
Assignment: Project 3
Description: Simulate a game of 20 questions using data output and retrieved from a file and using your
own binary decision tree.
File Name: main_SBach_00332111.java
Author: Sebastian Bach
Course: CIS 252 - Computer Science II
Instructor: Professor Penta
Semester: Fall 2022
Date: 19 November 2022
 */

import java.io.IOException;

public class TwentyQuestions
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("?????????? 20 Questions ??????????\n");

        // Declare and instantiate new DecisionTree, reconstruct preexisting tree from file,
        // and begin traversing the DecisionTree to play 20 questions.
        DecisionTree<String> newTree = new DecisionTree<>();
        newTree.readFromFile();
        newTree.traverseQuestions(newTree.root);
    }
}
