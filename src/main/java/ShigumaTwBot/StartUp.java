package ShigumaTwBot;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import java.util.List;

public class StartUp implements Runnable {

    private double GPULoad = 0;
    private double GPUtemp = 0;

    private Main main;

    public StartUp(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        if (this.main.enableNc) {
            Components components = JSensors.get.components();
            List<Gpu> GPUs = components.gpus;
            if (GPUs != null) {
                for (final Gpu gpu : GPUs) {
                    if (gpu.sensors != null) {
                        List<Load> loads = gpu.sensors.loads;
                        List<Temperature> temperatures = gpu.sensors.temperatures;
                        for (final Load load : loads) {
                            if (load.name.equals("Load GPU Core"))
                                GPULoad = load.value;
                        }
                        for (final Temperature temperature : temperatures) {
                            if (temperature.name.equals("Temp GPU Core")) {
                                GPUtemp = temperature.value;
                            }
                            GPUtemp = temperature.value;
                        }
                    }
                }
            }
            if (this.main.tmpName.isEmpty()) {
                String nameInfo = ("GPU:" + String.format("%.0f", GPULoad) + "%" + ":" + String.format("%.0f", GPUtemp) + "℃");
                this.main.asyncTwitter.updateProfile(this.main.defaultUsername + "[" + nameInfo + "]", "", "", "");
                System.out.println("[info] Changed Name from Properties");
            } else {
                String nameInfo = ("GPU:" + String.format("%.0f", GPULoad) + "%" + ":" + String.format("%.0f", GPUtemp) + "℃");
                this.main.asyncTwitter.updateProfile(this.main.tmpName + "[" + nameInfo + "]", "", "", "");
                System.out.println("[info] Changed Name from TmpName");
            }
        }
    }
}
