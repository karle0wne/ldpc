package ldpc.service;

import ldpc.entity.BooleanMatrix;
import ldpc.entity.GeneratingMatrix;
import ldpc.entity.LowDensityParityCheckMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для порождающей матрицы
 */
public class GeneratingMatrixService {

    public GeneratingMatrix createGeneratingMatrixFromParityCheckMatrix(LowDensityParityCheckMatrix ldpMatrix) {
        BooleanMatrix booleanLdpMatrix = ldpMatrix.getBooleanMatrix();

        BooleanMatrix booleanGeneratingMatrix = new BooleanMatrix(new ArrayList<List<Boolean>>());
        //TODO @e.kazakov, здесь логика для заполнения порождающей матрицы (booleanGeneratingMatrix), это твоя задача
        /*
        * 1. уберешь эти комментарии
        * 2. заменишь логикой получения порождающей матрицы из */


        return new GeneratingMatrix(booleanGeneratingMatrix);
    }
}
