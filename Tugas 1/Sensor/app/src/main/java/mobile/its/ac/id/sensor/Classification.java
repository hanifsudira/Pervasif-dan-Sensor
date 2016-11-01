package mobile.its.ac.id.sensor;

/**
 * Created by PEKANBARU on 11/1/2016.
 */

import android.util.Log;

import java.io.File;

import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import static android.content.ContentValues.TAG;

public class Classification {
    private Instances myData;
    private weka.classifiers.Classifier ibk;

    public void Train() throws Exception {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "Training.csv";
        String filePath = baseDir + File.separator  + fileName;

        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filePath);
        myData =  source.getDataSet();
        myData.setClassIndex(0);
        ibk = new IBk();
        ibk.buildClassifier(myData);;
    }

    public double classify(Instance inst) throws Exception {
        inst.setDataset(myData);
        return ibk.classifyInstance(inst);
    }
}
