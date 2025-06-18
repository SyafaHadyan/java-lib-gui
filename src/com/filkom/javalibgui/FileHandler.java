import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileHandler<T> {
    public BufferedReader loadFile(String filePath) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException fnfe) {
            File file = new File(filePath);

            file.createNewFile();
        }

        return bufferedReader;
    }

    public void writeFile(String filePath, List<T> list) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (T t : list) {
            System.err.println(t.toString());
            bufferedWriter.write(t.toString() + "\n");
        }

        bufferedWriter.flush();

        bufferedWriter.close();
        fileWriter.close();
    }
}
