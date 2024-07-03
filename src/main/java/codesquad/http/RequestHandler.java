package codesquad.http;

import codesquad.Main;
import codesquad.config.ResourcePathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ResourcePathManager resourcePathManager;

    public RequestHandler(ResourcePathManager resourcePathManager) {
        this.resourcePathManager = resourcePathManager;
    }

    public void handleRequest(Socket clientSocket) {
        try {
            OutputStream clientOutput = clientSocket.getOutputStream();
            List<String> request = readRequestHeader(clientSocket);

            if (request.isEmpty()) {
                clientOutput.write(new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR).makeResponse());
                clientOutput.flush();
            }

            logger.debug("request header: {}", request);

            HttpRequest httpRequest = new HttpRequest(request);
            boolean isCreated = createResponse(httpRequest, clientOutput);

            if (!isCreated) {
                clientOutput.write(new HttpResponse(HttpStatus.NOT_FOUND).makeResponse());
                clientOutput.flush();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private boolean createResponse(HttpRequest httpRequest, OutputStream clientOutput) throws IOException {
        String url = httpRequest.getUri();

        File file = null;

        for (String defaultPath : resourcePathManager.getFilePaths()) {
            String path = System.getProperty("user.dir") + File.separator + defaultPath + url;

            File curFile = new File(path);

            if (curFile.exists()) {
                file = curFile;
                break;
            }
        }

        if (file == null) {
            return false;
        }

        FileReader fileReader = new FileReader(file);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeHeader("Content-Type", fileReader.getContentType().getDirective());
        httpResponse.writeBody(fileReader.getBytes());
        clientOutput.write(httpResponse.makeResponse());
        clientOutput.flush();

        return true;
    }

    private List<String> readRequestHeader(Socket clientSocket) {
        List<String> request = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                request.add(line);
            }
            return request;
        } catch (IOException e) {
            logger.error("{}_{} - {}", getClass().getName(), "readRequestHeader", e.getMessage());
        }
        return request;
    }
}
