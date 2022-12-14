package hello.hellospring.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


@Controller
public class TicketController {

    @GetMapping("ticket")
    public String ticket(Model model) {
        model.addAttribute("data", "ticket");
        return "ticketmain";
    }

    @RequestMapping("/send2")
    public String send2(String startday,Model model){
        //model.addAttribute("day",startday);

        String clientId = "TP8GiRPSMSd67q5Ioip1"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "AS7oKyBvW4"; //애플리케이션 클라이언트 시크릿값"



        String text = null;


        text = URLEncoder.encode(startday, StandardCharsets.UTF_8);
        text = text.replaceAll("-","");
        int to = Integer.parseInt(text);
        to = to -7;
        String textresult = Integer.toString(to);



        String apiURL = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=f5eef3421c602c6cb7ea224104795888&targetDt=" + textresult;    // json 결과
        //String apiURL = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=f5eef3421c602c6cb7ea224104795888&targetDt=" + text;


        Map<String, String> requestHeaders = new HashMap<>();//
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        System.out.println(responseBody);
        model.addAttribute("jsonbody",responseBody);

        List<String> jsonlisttitle = new ArrayList<String>();
        //가장 큰 JSONObject를 가져옵니다.
        JSONObject jObject = new JSONObject(responseBody);

        JSONObject batters = jObject.getJSONObject("boxOfficeResult");
        JSONArray batter = batters.getJSONArray("dailyBoxOfficeList");

        for(int i=0; i < batter.length();i++){
            JSONObject obj = batter.getJSONObject(i);
            String title = obj.getString("movieNm");
            System.out.println("title(" + i + "): " + title);
            jsonlisttitle.add(title);
        }




        model.addAttribute("title",jsonlisttitle);



        return "ticket"; //
    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
