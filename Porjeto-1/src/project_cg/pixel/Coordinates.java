package project_cg.pixel;

public class Coordinates {

    public float calcularNdcx(float x, float xMin, float xMax, float coordenadaInicialX, float coordenadaFinalX) {
        float ndcx = ((x - xMin) / (xMax - xMin)) * (coordenadaFinalX - coordenadaInicialX) + coordenadaInicialX;
        return ndcx;
    }

    public float calcularNdcy(float y, float yMin, float yMax, float coordenadaInicialY, float coordenadaFinalY) {
        float ndcy = ((y - yMin) / (yMax - yMin)) * (coordenadaFinalY - coordenadaInicialY) + coordenadaInicialY;
        return ndcy;
    }

    public float[] inpToNdc(float x, float xMin, float xMax, float y, float yMin, float yMax, float coordenadaInicialX, float coordenadaFinalX, float coordenadaInicialY, float coordenadaFinalY) {
        float ndcx = calcularNdcx(x, xMin, xMax, coordenadaInicialX, coordenadaFinalX);
        float ndcy = calcularNdcy(y, yMin, yMax, coordenadaInicialY, coordenadaFinalY);
        float[] coords = {ndcx, ndcy};
        return coords;
    }

    public float calcularDcx(float ndcx, float ndh, float coordenadaInicialX, float coordenadaFinalX) {
        float dcx = Math.round(((ndcx - coordenadaInicialX) * (ndh - 1)) / (coordenadaFinalX - coordenadaInicialX));
        return dcx;
    }

    public float calcularDcy(float ndcy, float ndv, float coordenadaInicialY, float coordenadaFinalY) {
        float dcy = Math.round(((ndcy - coordenadaInicialY) * (ndv - 1)) / (coordenadaFinalY - coordenadaInicialY));
        return dcy;
    }

    public float[] ndcToDc(float ndcx, float ndcy, float ndh, float ndv, float coordenadaInicialX, float coordenadaFinalX, float coordenadaInicialY, float coordenadaFinalY) {
        float dcx = calcularDcx(ndcx, ndh, coordenadaInicialX, coordenadaFinalX);
        float dcy = calcularDcy(ndcy, ndv, coordenadaInicialY, coordenadaFinalY);
        float[] coords = {dcx, dcy};
        return coords;
    }

    public float[] userToNdc(float X, float Xm, float XM, float Y, float Ym, float YM) {
        float NDCX = (X - Xm) / (XM - Xm);
        float NDCY = (Y - Ym) / (YM - Ym);
        return new float[]{NDCX, NDCY};
    }

    public float[] ndcToUser(float NDCX, float Xm, float XM, float NDCY, float Ym, float YM) {
        float X = NDCX * (XM - Xm) + Xm;
        float Y = NDCY * (YM - Ym) + Ym;
        return new float[]{X, Y};
    }

}
