package semestrovka2;

public class TwoThreeTree {

    private long addOps; // количество операций для метода добавления
    private long removeOps; // количество операций для метода удаления
    private long searchOps; // количество операций для метода поиска


    public long getAddOps() {
        return addOps;
    }

    public long getRemoveOps() {
        return removeOps;
    }

    public long getSearchOps() {
        return searchOps;
    }

    // Внутренний класс для вершины дерева
    private class Node {
        int[] keys = new int[3]; // Массив для ключей
        Node[] children = new Node[4]; // Массив для дочерних вершин

        int keyCount = 0; // Количество ключей в вершине

        // Метод для проверки, является ли вершина листом
        boolean isLeaf() {
            return children[0] == null;
        }

        // Метод для проверки, является ли вершина полной
        boolean isFull() {
            return keyCount == 2;
        }

        // Метод для поиска индекса для вставки ключа
        int findIndex(int key) {
            for (int i = 0; i < keyCount; i++) {
                if (keys[i] > key) {
                    return i;
                }
            }
            return keyCount;
        }
    }

    private Node root = null; // Корневой элемент дерева

    // Метод для поиска ключа в дереве
    public boolean find(int key) {
        return find(root, key);
    }

    private boolean find(Node node, int key) {
        if (node == null) {
            return false;
        }

        for (int i = 0; i < node.keyCount; i++) {
            if (node.keys[i] == key) {
                return true;
            }

            if (node.keys[i] > key) {
                return find(node.children[i], key);
            }
        }
        return find(node.children[node.keyCount], key);
    }

    // Метод для вставки ключа в дерево
    public void insert(int key) {
        if (root == null) {
            root = new Node();
            root.keys[0] = key;
            root.keyCount = 1;
        } else {
            insert(root, key);
        }
    }

    private void insert(Node node, int key) {
        if (node.isLeaf()) {
            insertIntoLeaf(node, key);
        } else {
            int index = node.findIndex(key);
            insert(node.children[index], key);

            if (node.children[index].isFull()) {
                splitNode(node, index);
            }
        }
    }

    // Метод для вставки в лист
    private void insertIntoLeaf(Node node, int key) {
        int index = node.findIndex(key);

        for (int i = node.keyCount - 1; i >= index; i--) {
            node.keys[i + 1] = node.keys[i];
        }

        node.keys[index] = key;
        node.keyCount++;
    }

    // Метод для разделения узла
    private void splitNode(Node node, int index) {
        Node left = node.children[index];
        Node right = new Node();

        right.keys[0] = left.keys[2];
        right.keyCount = 1;

        left.keyCount = 1;

        if (!left.isLeaf()) {
            right.children[0] = left.children[2];
            right.children[1] = left.children[3];
            left.children[2] = null;
            left.children[3] = null;
        }

        for (int i = node.keyCount - 1; i >= index; i--) {
            node.children[i + 1] = node.children[i];
        }

        node.children[index + 1] = right;

        for (int i = node.keyCount - 1; i >= index; i--) {
            node.keys[i + 1] = node.keys[i];
        }

        node.keys[index] = left.keys[1];
        node.keyCount++;
    }

    // Метод для удаления ключа из дерева
    public void delete(int key) {
        delete(root, key);
        if (root.keyCount == 0 && !root.isLeaf()) {
            root = root.children[0];
        }
    }

    private void delete(Node node, int key) {
        if (node == null) {
            return;
        }

        for (int i = 0; i < node.keyCount; i++) {
            if (node.keys[i] == key) {
                removeKey(node, i);
                return;
            }

            if (node.keys[i] > key || i == node.keyCount - 1) {
                if (node.children[i].keyCount < 2) {
                    fixChild(node, i);
                }
                delete(node.children[i], key);
                break;
            }
        }
    }

    // Метод для удаления ключа из вершины
    private void removeKey(Node node, int index) {
        node.keyCount--;
        for (int i = index;
        i < node.keyCount; i++) {
            node.keys[i] = node.keys[i + 1];
        }

        node.keys[node.keyCount] = 0;
    }

    // Метод для корректировки дочернего элемента
    private void fixChild(Node node, int index) {
        Node child = node.children[index];

        if (index > 0 && node.children[index - 1].keyCount > 2) {
            borrowFromLeft(node, index);
        } else if (index < node.keyCount && node.children[index + 1].keyCount > 2) {
            borrowFromRight(node, index);
        } else {
            if (index == node.keyCount) {
                index--;
            }
            merge(node, index);
        }
    }

    // Метод для взятия ключей из левого дочернего элемента
    private void borrowFromLeft(Node node, int index) {
        Node child = node.children[index];
        Node leftChild = node.children[index - 1];

        for (int i = child.keyCount; i > 0; i--) {
            child.keys[i] = child.keys[i - 1];
        }

        if (!child.isLeaf()) {
            for (int i = child.keyCount + 1; i > 0; i--) {
                child.children[i] = child.children[i - 1];
            }
        }

        child.keyCount++;

        child.keys[0] = node.keys[index - 1];
        node.keys[index - 1] = leftChild.keys[leftChild.keyCount - 1];

        if (!leftChild.isLeaf()) {
            child.children[0] = leftChild.children[leftChild.keyCount];
            leftChild.children[leftChild.keyCount] = null;
        }

        leftChild.keyCount--;
    }

    // Метод для взятия ключей из правого дочернего элемента
    private void borrowFromRight(Node node, int index) {
        Node child = node.children[index];
        Node rightChild = node.children[index + 1];

        child.keys[child.keyCount] = node.keys[index];

        if (!child.isLeaf()) {
            child.children[child.keyCount + 1] = rightChild.children[0];
        }

        node.keys[index] = rightChild.keys[0];

        for (int i = 1; i < rightChild.keyCount; i++) {
            rightChild.keys[i - 1] = rightChild.keys[i];
        }

        if (!rightChild.isLeaf()) {
            for (int i = 1; i <= rightChild.keyCount; i++) {
                rightChild.children[i - 1] = rightChild.children[i];
            }
        }

        rightChild.keyCount--;
        child.keyCount++;
    }

    // Метод для объединения дочернего элемента с его соседом
    private void merge(Node node, int index) {
        Node leftChild = node.children[index];
        Node rightChild = node.children[index + 1];

        leftChild.keys[1] = node.keys[index];
        leftChild.keyCount = 2;

        for (int i = 0; i < rightChild.keyCount; i++) {
            leftChild.keys[i + 2] = rightChild.keys[i];
        }

        if (!leftChild.isLeaf()) {
            for (int i = 0; i <= rightChild.keyCount; i++) {
                leftChild.children[i + 2] = rightChild.children[i];
            }
        }

        leftChild.children[1] = rightChild.children[0];
        node.children[index + 1] = null;

        removeKey(node, index);
    }
}

