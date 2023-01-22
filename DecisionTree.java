/*
Assignment: Project 3
Description: Simulate a game of 20 questions using data output and retrieved from a file and using your
own binary decision tree.
File Name: DecisionTree.java
Author: Sebastian Bach
Course: CIS 252 - Computer Science II
Instructor: Professor Penta
Semester: Fall 2022
Date: 19 November 2022
 */

import java.io.*;
import java.util.*;
import ch04.queues.LinkedQueue;

public class DecisionTree<T>
{
    protected DecNode<T> root;
    protected DecNode<T> currentQuestion;
    protected boolean found;
    protected int numQuestions;

    protected DecTreeIterator<T> questIt;

    protected Scanner userInput;
    public enum Traversal {Inorder, Preorder, Postorder, BreadthFirst};

    public DecisionTree()
    {
        root = null;
        currentQuestion = null;
        found = false;
        numQuestions = 0;
        userInput = new Scanner(System.in);
    }

    public boolean isEmpty()
    {
        return (root == null);
    }

    public int size()
    {
        return numQuestions;
    }

    public boolean contains(T target)
    {
        questIt = getIterator(DecisionTree.Traversal.BreadthFirst);

        return recContains(target, questIt);
    }

    protected boolean recContains(T target, Iterator<T> iterator)
    {
        if(!questIt.hasNext())
        {
            found = false;
        }
        else
        {
            // See note in README file regarding (apparently) inescapable need for type casting.
            if(((String)questIt.next()).equals((String)target))
            {
                found = true;
            }
            else
            {
                return recContains(target, questIt);
            }
        }

        return found;
    }

    // Recursively finds target.
    public DecNode<T> find(T target, DecNode<T> question)
    {
        found = false;

        if(question == null)
        {
            found = false;
        }
        else
        {
            if(!question.getQuestion().toString().equals(target.toString()))
            {
                find(target, question.getNo());
                find(target, question.getYes());
            }
            else if(question.getQuestion().toString().equals(target.toString()))
            {
                currentQuestion = question;
                found = true;
            }
        }

        return currentQuestion;
    }

    public void add(T answer, T newQuestion, char yesOrNo) throws IOException
    {

        DecNode<T> newNoNode;
        DecNode<T> newYesNode;
        // Moves current question to the No branch and adds user answer to the Yes branch.
        if(yesOrNo == 'y')
        {
            newNoNode = new DecNode<>(currentQuestion.getQuestion());
            newNoNode.setYesNoBool(false);
            newNoNode.setLeaf(true);
            newYesNode = new DecNode<>(answer);
            newYesNode.setYesNoBool(true);
            newYesNode.setLeaf(true);
        }
        // Moves current question to the Yes branch and adds user answer to the No branch.
        else
        {
            newNoNode = new DecNode<>(answer);
            newNoNode.setYesNoBool(false);
            newNoNode.setLeaf(true);
            newYesNode = new DecNode<>(currentQuestion.getQuestion());
            newYesNode.setYesNoBool(true);
            newYesNode.setLeaf(true);
        }
        // Replaces current question with new user-supplied question, links new Yes and No nodes to
        // new question node, increments question counter, sets new question fullness flag to true,
        // changes new question leaf flag to false, sets parent of both new Yes and No nodes,
        // and assigns parent question value to parentStr for each child node.
        currentQuestion.setQuestion(newQuestion);
        currentQuestion.setYes(newYesNode);
        numQuestions++;
        currentQuestion.setNo(newNoNode);
        numQuestions++;
        currentQuestion.setFull(true);
        currentQuestion.setLeaf(false);
        newNoNode.setParent(currentQuestion);
        newNoNode.setParentStr(currentQuestion.getQuestion().toString());
        newYesNode.setParent(currentQuestion);
        newYesNode.setParentStr(currentQuestion.getQuestion().toString());

        // Writes tree structure changes to file.
        writeToFile();
    }

    // Traverses the tree, asking questions as it reaches each node.
    public void traverseQuestions(DecNode<T> question) throws IOException
    {
        String userResponse = "";
        currentQuestion = question;

        userResponse = validateResponse(currentQuestion);

        if(userResponse.toLowerCase().equals("yes") || userResponse.toLowerCase().equals("y"))
        {
            if(currentQuestion.getYes() != null)
            {
                traverseQuestions(currentQuestion.getYes());
            }
            else
            {
                // Does a mediocre impression of Forrest Gump and exits the program.
                System.out.println("Computer: That's all I have to say about that. Thanks for playing!");
                System.exit(0);
            }
        }
        else if(userResponse.toLowerCase().equals("no") || userResponse.toLowerCase().equals("n"))
        {
            if(currentQuestion.getNo() != null)
            {
                traverseQuestions(currentQuestion.getNo());
            }
            else
            {
                prepAdd(currentQuestion);
                System.out.println("Computer: Thanks for the new information. I will try to do better next time. Thanks for playing!");
                System.exit(0);
            }
        }

    }

    // Collects user input in response to computer's guess.
    protected String validateResponse(DecNode<T> question)
    {
        String response = "";
        boolean isValid = false;

        while(!isValid)
        {
            System.out.print("Computer: " + currentQuestion.getQuestion() + "\nYou (Yes/No): ");
            try
            {
                response = userInput.nextLine();
                response = response.trim();
            }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            if(response.toLowerCase().equals("yes") || response.toLowerCase().equals("y") || response.toLowerCase().equals("no") || response.toLowerCase().equals("n"))
            {
                isValid = true;
            }
            else
            {
                System.out.println("Invalid input. Please try again.");
            }
        }

        return response;
    }

    // Collects all necessary user input to pass as parameters to add().
    protected void prepAdd(DecNode<T> question) throws IOException
    {
        String answer, newQuestion;
        char yesNo = ' ';
        boolean hasTheAnswer;
        currentQuestion = question;

        answer = validateAnswer();
        // See note in README file regarding (apparently) inescapable need for type casting.
        hasTheAnswer = contains((T)answer);
        if(hasTheAnswer)
        {
            System.out.println("Computer: The answer you had in mind exists elsewhere in my decision tree.");
        }
        newQuestion = validateNewQuestion();
        yesNo = validateChildBranch();

        add((T)answer, (T)newQuestion, yesNo);
    }

    // Collects and validates user input that introduces a new answer into the tree.
    protected String validateAnswer()
    {
        String ans = "";
        boolean isValid = false;

        while(!isValid)
        {
            System.out.print("Please enter the answer you had in mind using an appropriate article (a/an/the): ");
            try
            {
                ans = userInput.nextLine();
                ans = ans.trim();
                ans = "Is it " + ans + "?";
                isValid = true;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid input. Please try again.");
            }
        }

        return ans;
    }

    // Collects and validates user input that introduces a new question into the tree.
    protected String validateNewQuestion()
    {
        String newQuest = "";
        boolean isValid = false;

        while(!isValid)
        {
            System.out.println("Please enter a new yes/no question that would help distinguish your answer from my guess");
            System.out.print("(Please respond with a question that begins with \"Is it...\"): ");

            try
            {
                newQuest = userInput.nextLine();
                newQuest = newQuest.trim();
                isValid = true;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid input. Please try again.");
            }
        }

        return newQuest;
    }

    // Collects and validates user input that determines whether or not the new answer belongs on a
    // Yes branch or a No branch.
    protected char validateChildBranch()
    {
        String yesOrNo = "";
        char result = ' ';
        boolean isValid = false;

        while(!isValid)
        {
            System.out.print("Is the answer you provided the result of a \"Yes\" or \"No\" response? ");

            try
            {
                yesOrNo = userInput.nextLine();
                yesOrNo = yesOrNo.trim();
                isValid = true;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid input. Please try again.");
            }
        }

        if(yesOrNo.equalsIgnoreCase("yes") || yesOrNo.equalsIgnoreCase("y"))
        {
            result = 'y';
        }
        else if(yesOrNo.equalsIgnoreCase("no") || yesOrNo.equalsIgnoreCase("n"))
        {
            result = 'n';
        }

        return result;
    }

    // Stringifies tree in preparation for writing structure to file.
    @Override
    public String toString()
    {
        StringBuilder buildMyString = new StringBuilder();
        DecNode<T> temp;
        questIt = getIterator(Traversal.BreadthFirst);

        while(questIt.hasNext())
        {
            temp = questIt.nextQuestion();

            buildMyString.append(temp.getQuestion()).append("|");

            if(temp.getParent() == null)
            {
                buildMyString.append("null").append("|");
            }
            else
            {
                buildMyString.append(temp.getParent().getQuestion().toString()).append("|");
            }

            if(temp.getNo() == null)
            {
                buildMyString.append("null").append("|");
            }
            else
            {
                buildMyString.append(temp.getNo().getQuestion()).append("|");
            }

            if(temp.getYes() == null)
            {
                buildMyString.append("null").append("|");
            }
            else
            {
                buildMyString.append(temp.getYes().getQuestion()).append("|");
            }

            buildMyString.append(temp.getYesNoBool()).append("|").append(temp.getFull()).append("|").append(temp.getLeaf()).append("\n");

        }

        return buildMyString.toString();
    }

    // Writes tree structure to file.
    protected void writeToFile() throws IOException
    {
        FileWriter fileHandle = null;
        String fileOutput;

        try
        {
            fileHandle = new FileWriter("DecisionTree.txt");
        }
        catch(FileNotFoundException noFile)
        {
            System.out.println("File \"DecisionTree.txt\" not found.");
        }

        fileOutput = toString();

        assert fileHandle != null;
        fileHandle.write(fileOutput);
        fileHandle.close();
    }

    // Reads tree structure from file and sends contents to parsing function.
    // Assigns return value of call to reconstructTree() to DecisionTree root.
    protected void readFromFile() throws IOException
    {
        File fileHandle = new File("DecisionTree.txt");
        Scanner fileReader = new Scanner(fileHandle);
        LinkedQueue<DecNode<T>> reconstructor = new LinkedQueue<>();
        String nodeLine;

        while(fileReader.hasNextLine())
        {
            nodeLine = fileReader.nextLine();
            nodeLine = nodeLine.trim();
            DecNode<T> newQuestion = parseLines(nodeLine);
            reconstructor.enqueue(newQuestion);
        }

        root = reconstructTree(reconstructor);
    }

    // Parses each node from stringified tree structure.
    protected DecNode<T> parseLines(String line)
    {
        String[] elementStager = new String[7];
        DecNode<T> newQuestion;

        elementStager = line.split("\\|");

        newQuestion = new DecNode<>((T)elementStager[0]);

        if(!elementStager[1].equals("null"))
        {
            newQuestion.setParentStr(elementStager[1]);
        }

        if(elementStager[4].equals("true"))
        {
            newQuestion.setYesNoBool(true);
        }
        else
        {
            newQuestion.setYesNoBool(false);
        }

        if(elementStager[5].equals("true"))
        {
            newQuestion.setFull(true);
        }
        else
        {
            newQuestion.setFull(false);
        }

        if(elementStager[6].equals("true"))
        {
            newQuestion.setLeaf(true);
        }
        else
        {
            newQuestion.setLeaf(false);
        }

        return newQuestion;
    }

    // Reconstructs DecisionTree based on contents of LinkedQueue.
    protected DecNode<T> reconstructTree(LinkedQueue<DecNode<T>> treeQ)
    {
        DecisionTree<T> reconstructedTree = new DecisionTree<>();
        DecNode<T> temp = null;
        numQuestions = 0;

        reconstructedTree.root = treeQ.dequeue();
        numQuestions++;

        reconstructedTree.currentQuestion = reconstructedTree.root;

        while(!treeQ.isEmpty())
        {
            if(temp == null)
            {
                temp = treeQ.dequeue();
            }


            if(reconstructedTree.currentQuestion.getNo() == null && !temp.getYesNoBool())
            {
                temp.setParent(currentQuestion);
                reconstructedTree.currentQuestion.setNo(temp);
                numQuestions++;

                if(reconstructedTree.currentQuestion.getYes() != null && reconstructedTree.currentQuestion.getNo() !=null)
                {
                    reconstructedTree.currentQuestion.setFull(true);
                }

                temp = null;
            }
            else if(reconstructedTree.currentQuestion.getYes() == null && temp.getYesNoBool())
            {
                temp.setParent(currentQuestion);
                reconstructedTree.currentQuestion.setYes(temp);
                numQuestions++;

                if(reconstructedTree.currentQuestion.getNo() != null && reconstructedTree.currentQuestion.getNo() !=null)
                {
                    reconstructedTree.currentQuestion.setFull(true);
                }

                temp = null;
            }
            else if(reconstructedTree.currentQuestion.getFull() && !reconstructedTree.currentQuestion.getQuestion().toString().equals(temp.getParentStr()))
            {
                reconstructedTree.currentQuestion = find((T)temp.getParentStr(), reconstructedTree.root);

                if(found && temp.getYesNoBool())
                {
                    temp.setParent(reconstructedTree.currentQuestion);
                    reconstructedTree.currentQuestion.setYes(temp);
                    numQuestions++;
                    temp = null;
                }
                else if(found && !temp.getYesNoBool())
                {
                    temp.setParent(reconstructedTree.currentQuestion);
                    reconstructedTree.currentQuestion.setNo(temp);
                    numQuestions++;
                    temp = null;
                }
            }
        }

        return reconstructedTree.root;
    }

    // Extends Iterator interface to modify next() (as nextQuestion()) to return entire nodes/questions
    // instead of merely node/question values (T/String). Doing so aids with the tree stringification process.
    public DecTreeIterator<T> getIterator(DecisionTree.Traversal orderType)
    {
        LinkedQueue<DecNode<T>> questionQueue = new LinkedQueue<DecNode<T>>();

        if(orderType == DecisionTree.Traversal.Preorder)
        {
            preOrder(root, questionQueue);
        }
        else if(orderType == DecisionTree.Traversal.Inorder)
        {
            inOrder(root, questionQueue);
        }
        else if(orderType == DecisionTree.Traversal.Postorder)
        {
            postOrder(root, questionQueue);
        }
        else if(orderType == DecisionTree.Traversal.BreadthFirst)
        {
            breadthFirst(root, questionQueue);
        }

        return new DecTreeIterator<T>()
        {
            public boolean hasNext()
            {
                return !questionQueue.isEmpty();
            }

            public T next()
            {
                if(!hasNext())
                {
                    throw new IndexOutOfBoundsException("illegal invocation of next in DecisionTree iterator.\n");
                }

                return questionQueue.dequeue().getQuestion();
            }

            public DecNode<T> nextQuestion()
            {
                if(!hasNext())
                {
                    throw new IndexOutOfBoundsException("illegal invocation of next in DecisionTree iterator.\n");
                }

                return questionQueue.dequeue();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Unsupported remove attempted on DecisionTree iterator.\n");
            }
        };
    }

    private void preOrder(DecNode<T> question, LinkedQueue<DecNode<T>> questQueue)
    {
        if(question != null)
        {
            questQueue.enqueue(question);
            preOrder(question.getNo(), questQueue);
            preOrder(question.getYes(), questQueue);
        }
    }

    private void inOrder(DecNode<T> question, LinkedQueue<DecNode<T>> questQueue)
    {
        if(question != null)
        {
            inOrder(question.getNo(), questQueue);
            questQueue.enqueue(question);
            inOrder(question.getYes(), questQueue);
        }
    }

    private void postOrder(DecNode<T> question, LinkedQueue<DecNode<T>> questQueue)
    {
        if(question != null)
        {
            postOrder(question.getNo(), questQueue);
            postOrder(question.getYes(), questQueue);
            questQueue.enqueue(question);
        }
    }

    // New orderType based on textbook's abstract description.
    private void breadthFirst(DecNode<T> question, LinkedQueue<DecNode<T>> questQueue)
    {
        DecNode<T> tempNode;
        LinkedQueue<DecNode<T>> tempQueue = new LinkedQueue<>();

        if(question != null)
        {
            tempQueue.enqueue(root);

            while(!tempQueue.isEmpty())
            {
                tempNode = tempQueue.dequeue();
                questQueue.enqueue(tempNode);
                if(tempNode.getNo() != null)
                {
                    tempQueue.enqueue(tempNode.getNo());
                }

                if(tempNode.getYes() != null)
                {
                    tempQueue.enqueue(tempNode.getYes());
                }
            }
        }
    }

    public DecTreeIterator<T> iterator()
    {
        return getIterator(DecisionTree.Traversal.Preorder);
    }

}
