package grefitcom.grefit.utils;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 江南 on 2015/10/22.
 */
public class WatsonSTT {

    public static String STT_USERNAME="eb71c508-6e36-4e3f-9a9f-a2c04579b252";
    public static String STT_PASSWORD="KXAyFoQkg6to";
//    "username": "eb71c508-6e36-4e3f-9a9f-a2c04579b252",
//            "password": "KXAyFoQkg6to"
    /**
     * 将语音文件转化为文字(改函数将英文语言翻译英文)
     * @param audioUrl  要转换为文字的语音文件路径
     * @return
     */
    public static String speechToText(String audioUrl){

        SpeechToText service = new SpeechToText();
        //service.setUsernameAndPassword("cedf80b0-7834-4c6d-bf1f-2f97cbfc9b61", "bhRWf6nInRrm");
        service.setUsernameAndPassword(STT_USERNAME,STT_PASSWORD);

        File audio = new File(audioUrl);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("audio", audio);
        params.put("content_type", "audio/wav; rate=16000"); //flac or war file format

        params.put("model", "zh-CN_BroadbandModel"); //english   zh-CN_BroadbandModel


        params.put("word_confidence", true);
        params.put("continuous", true);
        params.put("timestamps", true);
        params.put("inactivity_timeout", 1200);
        params.put("max_alternatives", 1);

        SpeechResults speechResults = service.recognize(params);
        List<Transcript> transcriptList=speechResults.getResults();
        SpeechAlternative speechAlternative=transcriptList.get(0).getAlternatives().get(0);

        return speechAlternative.getTranscript();

    }

}
