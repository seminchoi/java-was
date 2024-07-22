package codesquad.template;

import codesquad.exception.HttpException;
import codesquad.file.AppFileReader;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicHtml {
    private static final Logger logger = LoggerFactory.getLogger(DynamicHtml.class);

    private static final Pattern IF_BLOCK = Pattern.compile("\\{dh:if=\"\\$\\{(\\w+)\\}\"\\}([\\s\\S]*?)\\{end-if\\}\\s+(?:\\{dh:else\\}([\\s\\S]*?)\\{end-else\\})?");
    private static final Pattern EACH_BLOCK = Pattern.compile("\\{dh:each=\"(\\w+)\\s*:\\s*\\$\\{(\\w+)\\}\"\\}([\\s\\S]*?)\\{end-each\\}");
    private static final Pattern TEXT_BLOCK = Pattern.compile("\\{dh:text=\"\\$\\{(\\w+(?:\\.\\w+)?)\\}\"\\}");

    private String templatePathPrefix = "templates";

    private Map<String, Object> arguments = new HashMap<>();
    private String templateName;

    public void setArg(String key, Object value) {
        arguments.put(key, value);
    }

    public HttpResponse process(HttpStatus httpStatus) {
        AppFileReader fileReader = new AppFileReader(templatePathPrefix + templateName);
        String content = fileReader.getContent();

        content = processIfBlocks(content);
        content = processEachBlocks(content);
        content = processTextBlocks(content);

        HttpResponse httpResponse = new HttpResponse(httpStatus);
        httpResponse.writeBody(content.getBytes());

        return httpResponse;
    }

    public HttpResponse process() {
        return process(HttpStatus.OK);
    }

    private String processIfBlocks(String html) {
        Matcher matcher = IF_BLOCK.matcher(html);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String condition = matcher.group(1);
            String ifContent = matcher.group(2);
            String elseContent = matcher.groupCount() > 2 ? matcher.group(3) : "";

            Boolean conditionValue = (Boolean) arguments.get(condition);
            String replacement;

            if (conditionValue != null && conditionValue) {
                replacement = ifContent;
            } else if (elseContent != null && !elseContent.isEmpty()) {
                replacement = elseContent;
            } else {
                replacement = "";  // else 블록이 없는 경우 빈 문자열로 대체
            }

            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String processEachBlocks(String html) {
        Matcher matcher = EACH_BLOCK.matcher(html);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String itemName = matcher.group(1);
            String listName = matcher.group(2);
            String blockContent = matcher.group(3);

            List<?> items = (List<?>) arguments.get(listName);
            if (items == null) {
                matcher.appendReplacement(builder, "");
                continue;
            }

            StringBuilder replacement = new StringBuilder();
            for (Object item : items) {
                String processedBlock = processBlock(blockContent, itemName, item);
                replacement.append(processedBlock);
            }
            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement.toString()));

        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String processBlock(String block, String itemName, Object item) {
        //TODO: 구조 수정
        Pattern pattern = Pattern.compile("\\$\\{" + itemName + "\\.(\\w+)\\}");
        Matcher matcher = pattern.matcher(block);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = getFieldValue(item, fieldName);
            matcher.appendReplacement(builder, Matcher.quoteReplacement(fieldValue));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String processTextBlocks(String html) {
        Matcher matcher = TEXT_BLOCK.matcher(html);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String[] content = matcher.group(1).split("\\.");
            String itemName = content[0];
            Object item = arguments.get(itemName);
            String fieldValue = "";
            if (item != null) {
                fieldValue = item.toString();
                if (content.length == 2) {
                    fieldValue = getFieldValue(item, content[1]);
                }
            }
            matcher.appendReplacement(builder, fieldValue);
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }
}