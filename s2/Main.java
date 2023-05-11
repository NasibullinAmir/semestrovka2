package semestrovka2;



public class Main {
    public static void main(String[] args) {
        TwoThreeTree tree = new TwoThreeTree();

        // Добавление элементов и подсчет операций для метода insert()
        for (int i = 1; i <= 10000; i++) {
            tree.insert(i);
        }
        System.out.println("Add ops: " + tree.getAddOps());

        // Удаление элементов и подсчет операций для метода delete()
        for (int i = 1; i <= 1000; i++) {
            tree.delete(i);
        }
        System.out.println("Remove ops: " + tree.getRemoveOps());

        // Поиск элементов и подсчет операций для метода search()
        for (int i = 1; i <= 1000; i++) {
            tree.find(i);
        }
        System.out.println("Search ops: " + tree.getSearchOps());
    }
}