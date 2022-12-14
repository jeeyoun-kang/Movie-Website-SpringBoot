package hello.hellospring.controller;

import org.json.JSONArray;
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
import org.json.JSONObject;


@Controller
public class SearchController {
    @GetMapping("search")
    public String ticket(Model model) {
        model.addAttribute("data", "search");
        return "search";//
    }

    @GetMapping("searchfirst")
    public String ticketfirst(Model model) {
        model.addAttribute("data", "search");
        return "searchfirst";//
    }

    @RequestMapping("/send1")
    public String send1(String moviename,Model model){

        String clientId = "TP8GiRPSMSd67q5Ioip1"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "AS7oKyBvW4"; //애플리케이션 클라이언트 시크릿값"

        String text = null;
        text = URLEncoder.encode(moviename, StandardCharsets.UTF_8);

        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;    // json 결과
        //String apiURL = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=f5eef3421c602c6cb7ea224104795888&targetDt=" + text;


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        model.addAttribute("jsonbody",responseBody);
        System.out.println(responseBody);

        // 가장 큰 JSONObject를 가져옵니다.
        JSONObject jObject = new JSONObject(responseBody);
        // 배열을 가져옵니다.
        JSONArray jArray = jObject.getJSONArray("items");

        List<String> jsonlisttitle = new ArrayList<String>();
        List<String> jsonlistimage = new ArrayList<String>();
        List<String> jsonlistdi = new ArrayList<String>();

        // 배열의 모든 아이템을 출력합니다.
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = jArray.getJSONObject(i);
            String title = obj.getString("title");
            title = title.replaceAll("\\<.*?>","");
            title = title.replaceAll("&amp;","&");
            String image = obj.getString("image");

//            boolean draft = obj.getBoolean("draft");
            //System.out.println("title(" + i + "): " + title);
            //System.out.println("image(" + i + "): " + image);
//            System.out.println("draft(" + i + "): " + draft);
            jsonlisttitle.add(title);

            jsonlistimage.add(image);
            System.out.println();
        }

        //System.out.println(jsonlisttitle.get(1));

        model.addAttribute("title1",jsonlisttitle.get(0));
        model.addAttribute("image1",jsonlistimage.get(0));


        return "search"; //
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



