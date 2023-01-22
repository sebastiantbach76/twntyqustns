/*
Assignment: Project 3
Description: Simulate a game of 20 questions using data output and retrieved from a file and using your
own binary decision tree.
File Name: DecNode.java
Author: Sebastian Bach
Course: CIS 252 - Computer Science II
Instructor: Professor Penta
Semester: Fall 2022
Date: 19 November 2022
 */

public class DecNode<T>
{
    private T question;
    private DecNode<T> yes;
    private DecNode<T> no;
    private DecNode<T> parent;

    // A String and Boolean values that will store vitally important information that must survive
    // the tree stringification and reconstruction process.
    private String parentStr;

    private boolean isYesNode;

    private boolean isFull;

    private boolean isLeaf;

    public DecNode(T question)
    {
        this.question = question;
        yes = null;
        no = null;
        parent = null;
        parentStr = null;
        isYesNode = false;
        isFull = false;
        isLeaf = true;
    }

    public void setQuestion(T question) {this.question = question;}
    public T getQuestion() {return question;}

    public void setYes(DecNode<T> yesNode) {yes = yesNode;}
    public void setNo(DecNode<T> noNode) {no = noNode;}
    public void setParent(DecNode<T> parentNode) {parent = parentNode;}

    public void setParentStr(String parentString) { parentStr = parentString; }

    public String getParentStr() { return parentStr; }

    public DecNode<T> getYes() {return yes;}
    public DecNode<T> getNo() {return no;}
    public DecNode<T> getParent() {return parent;}

    public void setYesNoBool(boolean yesNo) { isYesNode = yesNo; }

    public boolean getYesNoBool() { return isYesNode; }

    public void setFull(boolean fullStatus) { isFull = fullStatus; }

    public boolean getFull() { return isFull; }

    public void setLeaf(boolean leafStatus) { isLeaf = leafStatus; }

    public boolean getLeaf() { return isLeaf; }
}
