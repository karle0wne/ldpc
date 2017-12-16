package ldpc.util.service;

import ldpc.util.template.ColumnPair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnPairService {

    public String arrayToString(List<ColumnPair> swapHistory) {
        String title = "ИСТОРИЯ ПЕРЕСТАНОВКИ СТОЛБЦОВ: ";
        String history = swapHistory.stream()
                .map(ColumnPair::toString)
                .collect(Collectors.joining("\n"));
        return String.join("\n", title, history);
    }
}
