package com.tianchi.garbage;

import com.alibaba.tianchi.garbage_image_util.ImageClassSink;
import com.alibaba.tianchi.garbage_image_util.ImageDirSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public class RunZoo3 {
    public static void main(String[] args) throws Exception {
        String modelPath = System.getenv("MODEL_INFERENCE_PATH")+"/SavedModel";
        boolean ifReverseInputChannels = true;
        int[] inputShape = {1, 224, 224, 3};
        //float[] meanValues = {123.68f, 116.78f, 103.94f};
        float[] meanValues = {103.94f, 116.78f, 123.68f};
        float scale = 1.0f;
        String input = "resnet50_input";

        ClassIndex.InitTable();
        Map<Integer, String> labelTable = ClassIndex.getTable();

        StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        flinkEnv.setParallelism(1);
        ImageDirSource source = new ImageDirSource();
        flinkEnv.addSource(source).setParallelism(1)
                .flatMap(new ModelPredictionMapFunction2(modelPath,inputShape,ifReverseInputChannels,meanValues,scale,
                        input,labelTable)).setParallelism(1)
                .addSink(new ImageClassSink()).setParallelism(1);
        flinkEnv.execute();
    }
}

