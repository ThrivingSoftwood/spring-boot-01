package thriving.softwood.sample.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplexTraceVO {
    private String traceId;
    private String finalMessage;
    private String cpuTaskResult;
    private String ioTaskResult;
    private long durationMs;
}