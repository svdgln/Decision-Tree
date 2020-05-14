import java.util.*;

public class decisionTree {

    public static String[][] data = {
            {"sunny", "sunny", "overcast", "rainy", "rainy", "rainy", "overcast", "sunny", "sunny", "rainy", "sunny", "overcast", "overcast","rainy"},
            {"hot", "hot", "hot", "mild", "cool", "cool", "cool", "mild", "cool", "mild", "mild", "mild", "hot","mild"},
            {"high", "high", "high", "high", "normal", "normal", "normal", "high", "normal", "normal", "normal", "high", "normal","high"},
            {"false", "true", "false", "false", "false", "true", "true", "false", "false", "false", "true", "true", "false","true"}
    };

    public static String [] Decision = {"no", "no", "yes", "yes", "yes", "no", "yes", "no", "yes", "yes", "yes", "yes", "yes","no"};

    public static String [] titles = {"Outlook", "Temperature", "Humidity", "Windy"};

    public static void main(String[] args) {

        int counter = 0;
        Node root = recursive_tree_creation(data, Decision, titles);
        counter = print_tree(root,counter);
        System.out.println("\nTotal node : "+counter);


    }

    public static double entropy(String[] attribute_list){
        /*
        Calculate the entropy of a dataset.
        The only parameter of this function is the attribute_list parameter which contains branches of the titles
        Mathematical formula for entropy :
        Entropy = -(probability(a) * log2(probability(a))) – (probability(b) * log2(probability(b)))
        */
        double result = 0.0;
        Integer[] amounts = uniqueCount(attribute_list);

        double[] fraction_amounts = new double[amounts.length];

        for(int i=0;i<amounts.length;i++){
            fraction_amounts[i] = (double)amounts[i]/attribute_list.length;
        }

        for(int i=0;i<fraction_amounts.length;i++){
            if(fraction_amounts[i] !=0){
                result -= (double)fraction_amounts[i] *(double) Math.log(fraction_amounts[i])/Math.log(2);
            }
        }

        return result;

    }


    public static double info_gain(String [] class_attribute, String [] attribute){
        /*
        Calculates the information gain of a dataset.
        Finds entropy of each attribute list the entropy function is passed a list of class_attribute values in a list.

        */

        double result = entropy(class_attribute);
        Integer[] amounts = uniqueCount(attribute); // amounts array shows the amount of times each type of value occurs
        double[] fraction_amounts = new double[amounts.length];
        String[] value = get_unique_array(attribute); // value array shows unique Strings


        for(int i=0;i<amounts.length;i++){
            fraction_amounts[i] = (double)amounts[i]/attribute.length;
        }

        for(int i=0; i<fraction_amounts.length; i++){
            String[] array = new String[amounts[i]];
            int index = 0;
            for(int j=0; j<attribute.length; j++){


                if(attribute[j]==(value[i])){
                    array[index] = class_attribute[j];
                    index++;
                }

            }
            result -= fraction_amounts[i] * entropy(array);

        }

        return result;
    }


    public static HashMap<String, Integer[]> split(String[] attribute){
        HashMap<String, Integer[]> indicates  = new HashMap<>();
        String [] unique = get_unique_array(attribute);
        Integer[] amounts = uniqueCount(attribute);

        for(int i=0; i<unique.length;i++){
            for(int j=0;j<attribute.length;j++){
                if(attribute[j]==unique[i]){
                    Integer[] indexes = findIndexes(attribute,unique[i]);
                    indicates.put(unique[i],indexes);
                }
            }
        }

        return indicates;
    }


    public static Integer[] findIndexes(String[] attribute,String a){

        int index = 0;
        int count = 0;
        for(int i=0;i<attribute.length;i++){
            if(attribute[i]==a){
                count++;
            }

        }
        Integer[] array = new Integer[count];

        for(int j=0;j<attribute.length;j++){
            if(attribute[j]==a){
                array[index] = j;
                index++;
            }

        }

        return array;
    }

    public static Node recursive_tree_creation(String [][] attributes,String [] class_attributes, String[] featureList ){
        /*ID3 Algorithm:
        1.Find the maximum information gain among all the features
        Example : Outlook in our example and it has three branches: Sunny, Overcast and Rainy.
        2.Remove the feature assigned in root node from the feature list
        and again find the maximum increase in information gain for each branch.
        3.Assign the feature as child  node of each branch and remove that feature from featurelist for that branch.
        Example : Sunny Branch for outlook root node has humidity as child node.
        4.Repeat step 2-3 until you get branches with only pure leaf. In our example, either yes or no.
        * */
        if(class_attributes.length == 0 || isPure(class_attributes)){
            return new Node(class_attributes);
        }

        double all_gains[] = new double[attributes.length];
        for(int i = 0; i < all_gains.length; i++) {
            all_gains[i] = 0;
        }
        for(int i=0; i < all_gains.length; i++) {
            all_gains[i] = info_gain(class_attributes,attributes[i]);
        }

        int selected_attribute = 0;
        double max = all_gains[0];

        for(int i = 1; i < all_gains.length;i++){

                if(all_gains[i] > max){
                    max = all_gains[i];
                    selected_attribute = i;}
            }



        HashMap<String,Integer[]> dict = split(attributes[selected_attribute]);
        String title = featureList[selected_attribute];

        //remove the selected attribute from the data
        attributes = removeRow(attributes,selected_attribute);
        featureList = removeFeature(featureList,selected_attribute);

        Node root = new Node(class_attributes);
        root.title = title;



        for (Map.Entry<String, Integer[]> entry : dict.entrySet()) {


            String [] class_attribute_subset = new String[entry.getValue().length];


            for(int i = 0; i < class_attribute_subset.length; i++){
                class_attribute_subset[i]= class_attributes[entry.getValue()[i]];
            }

            //create a new data array with the selected attribute column removed.
            List<String[]> new_data = new ArrayList<>();

            for(int i=0; i<attributes.length; i++){
                String[] str = new String[entry.getValue().length];
                for(int j =0; j<entry.getValue().length; j++) {

                    str[j] = attributes[i][entry.getValue()[j]];
                }
                new_data.add(str);
            }


            String[][] n_data = new String[new_data.size()][];
            for (int i = 0; i < n_data.length; i++) {
                n_data[i] = new String[new_data.get(i).length];
            }
            for(int i=0; i<new_data.size(); i++){
                for (int j = 0; j < new_data.get(i).length; j++) {
                    n_data[i][j] = new_data.get(i)[j];
                }
            }

            //recursive step to create tree and pass in split data after
            Node child = recursive_tree_creation(n_data,class_attribute_subset,featureList);
            child.add_branch_name(entry.getKey());
            root.add_chid(child);

        }

        return root;
    }

    public static String[] removeFeature(String[] data, int selected) {
        String[] realCopy = new String[data.length - 1];
        for (int i = 0, k = 0; i < data.length; i++) {
            if (i == selected) {
                continue;
            }
            realCopy[k++] = data[i];

        }
        return realCopy;
    }

    public static String[][] removeRow(String[][] data, int selected)
    {
        String[][] realCopy = new String[data.length-1][data[0].length];
        int count = 0;
        if(selected==data.length-1)
        {
            for(int r=0; r<data.length-1; r++)
            {
                for(int c=0; c<data[0].length; c++)
                {
                    if(selected==r)
                        r++;
                    realCopy[count][c]= data[r][c];
                }
                count++;
            }
        }
        else
        {
            for(int r=0; r<data.length; r++)
            {
                for(int c=0; c<data[0].length; c++)
                {
                    if(selected==r)
                        r++;
                    realCopy[count][c]= data[r][c];
                }
                count++;
            }
        }
        return realCopy;
    }


    public static int print_tree(Node node,int counter ){

        if(node.children.isEmpty()){
            System.out.println();
            System.out.println("\nBranch : " + node.branch_name);
            node.data = get_unique_array(node.data);
            System.out.print("Values : ");
            for(int i=0; i< node.data.length; i++) {
                System.out.print(node.data[i] + " ");
            }
            System.out.println("\nTerminal node " );

            return counter;
        }

        if(!node.branch_name.equals("")){
            System.out.println();
            System.out.println("\nBranch: " + node.branch_name );

        }
        if(!node.title.equals("")){
            System.out.println("Title: " + node.title);
        }

        System.out.print("Possible decision (play?) : ");
        for(int i=0; i<node.data.length; i++){
            System.out.print(node.data[i] + " ");
        }
        counter++;
        for(Node i : node.children){
            counter = print_tree(i,counter);
            counter++;
        }

        return counter;
    }

    public static Integer[] uniqueCount(String [] attribute_list){
        String[] uniq = get_unique_array(attribute_list);
        Integer[] count = new Integer[uniq.length];
        for(int j=0;j<uniq.length;j++){
            count[j]=0;
        }

        for(int j=0;j<uniq.length;j++){
            for(int i=0;i< attribute_list.length;i++){
                if(attribute_list[i]==uniq[j])
                    count[j]++;
            }
        }
        return count;

    }
    public static String[] get_unique_array(String[] attribute_list){
        Set<String> uniqKeys = new TreeSet<String>();
        uniqKeys.addAll(Arrays.asList(attribute_list));

        String[] uniqArray = new String[uniqKeys.size()];

        int index = 0;
        for (String str : uniqKeys)
            uniqArray[index++] = str;

        return uniqArray;


    }


    public static boolean isPure(String[] class_attribute){
        String [] classAttribute = get_unique_array(class_attribute);
        if(classAttribute.length == 1){
            return true;
        }
        return false;
    }



}

class Node{
    String [] data;
    ArrayList<Node> children;
    String branch_name;
    String title;
    Node(String [] d){
        this.data = d;
        this.children = new ArrayList<Node>();
        this.branch_name = "";
        this.title = "";

    }
    public void add_chid(Node obj){
        this.children.add(obj);
    }
    public void add_branch_name(String name){
        this.branch_name = name;
    }
}








