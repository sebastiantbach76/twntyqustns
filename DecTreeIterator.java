/*
Assignment: Project 3
Description: Simulate a game of 20 questions using data output and retrieved from a file and using your
own binary decision tree.
File Name: DecTreeIterator_SBach_00332111.java
Author: Sebastian Bach
Course: CIS 252 - Computer Science II
Instructor: Professor Penta
Semester: Fall 2022
Date: 19 November 2022
 */

import java.util.Iterator;

// Extends Iterator interface to modify next() (as nextQuestion()) to return entire nodes/questions
// instead of merely node/question values (T/String). Doing so aids with the tree stringification process.
public interface DecTreeIterator<T> extends Iterator<T>
{
    DecNode<T> nextQuestion();
}
