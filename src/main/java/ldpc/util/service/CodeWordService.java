package ldpc.util.service;

import ldpc.util.template.CodeWord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeWordService {

    public CodeWord newCodeWord(List<Double> softMetrics) {
        return new CodeWord(newElements(softMetrics));
    }

    private List<Double> newElements(List<Double> elements) {
        return new ArrayList<>(elements);
    }
}
