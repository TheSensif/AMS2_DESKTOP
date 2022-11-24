package modules;

import java.util.HashMap;
import java.util.Objects;

public class DataModule {
    private String etiqueta;
    private String id;
    private String defaul;
    private int min;
    private int max;
    private String step;
    private HashMap<String,String> value;
    private String label;
    private String units;
    private int thresholdlow;
    private int thresholdhigh;
    private String name;

    public DataModule(String etiqueta,String id, String defaul, String name) {
        this.etiqueta = etiqueta;
        this.id = id;
        this.defaul = defaul;
        this.name = name;
    }

    public DataModule (String etiqueta,String id, String defaul, int min, int max, String step, String name) {
        this.etiqueta = etiqueta;
        this.id = id;
        this.defaul = defaul;
        this.min = min;
        this.max = max;
        this.step = step;
        this.name = name;
    }

    public DataModule (String etiqueta, String id, String defaul, String label, HashMap<String,String> value) {
        this.etiqueta = etiqueta;
        this.id = id;
        this.defaul = defaul;
        this.label = label;
        this.value = value;
    }

    public DataModule (String etiqueta,String id, String units, int thresholdlow, int thresholdhigh, String nom) {
        this.etiqueta = etiqueta;
        this.id = id;
        this.units = units;
        this.thresholdlow = thresholdlow;
        this.thresholdhigh = thresholdhigh;
        this.name = nom;
    }

    //This will be the contrsuctor used when we want to create a block Hashmap
    public DataModule(String name) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaul() {
        return defaul;
    }

    public void setDefaul(String defaul) {
        this.defaul = defaul;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public HashMap<String, String> getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getThresholdlow() {
        return thresholdlow;
    }

    public void setThresholdlow(int thresholdlow) {
        this.thresholdlow = thresholdlow;
    }

    public int getThresholdhigh() {
        return thresholdhigh;
    }

    public void setThresholdhigh(int thresholdhigh) {
        this.thresholdhigh = thresholdhigh;
    }

    public void setValue(HashMap<String,String> value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        if (Objects.equals(etiqueta, "slider")) {
            return "DataModule{" + etiqueta +
                    ", id='" + id +
                    ", defaul='" + defaul + '\'' +
                    ", min=" + min +
                    ", max=" + max +
                    ", step='" + step + '\'' +
                    '}';
        } else  if (Objects.equals(etiqueta, "switch")){
            return "DataModule{" + etiqueta+
                    ", id='" + id + '\'' +
                    ", defaul='" + defaul + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        } else if (Objects.equals(etiqueta, "dropdown")) {
            return "DataModule{" + etiqueta + '\'' +
                    ", id='" + id + '\'' +
                    ", defaul='" + defaul + '\'' +
                    ", label='" + label + '\'' +
                    ", value=" + value + '}';
        } else if (Objects.equals(etiqueta, "sensor")) {
            return "DataModule{" +
                    "etiqueta='" + etiqueta + '\'' +
                    ", id='" + id + '\'' +
                    ", units='" + units + '\'' +
                    ", thresholdlow=" + thresholdlow +
                    ", thresholdhigh=" + thresholdhigh +
                    ", name='" + name + '\'' +
                    '}';
        }

        return null;
    }

}
