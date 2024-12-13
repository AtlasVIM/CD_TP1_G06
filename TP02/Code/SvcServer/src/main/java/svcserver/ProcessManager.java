package svcserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessManager {

    private static final ConcurrentHashMap<String, Process> processes = new ConcurrentHashMap<>(); //Gerenciar a concorrencia
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void addNewProcess(String id, int totalChunks){
        executor.submit(() -> {
            processes.putIfAbsent(id, new Process(id, totalChunks));
        });
    }

    public static Process getProcess(String id){
        return processes.get(id);
    }

    public static void setChunkUploadRequestObject(String id, byte[] chunk){
        executor.submit(() -> {
            processes.computeIfPresent(id, (key, process) ->{
                synchronized (process){
                   try {
                       ByteArrayOutputStream outputStream = process.getUploadRequestObject();
                       outputStream.write(chunk);
                       return process;
                   } catch (IOException e) {
                       e.printStackTrace();
                       return process;
                   }
                }
           });
        });
    }

    public static void setChunks(String id, int chunkIndex){
        executor.submit(() -> {
            processes.computeIfPresent(id, (key, process) ->{
                process.setChunkIndex(chunkIndex);
                return process;
            });
        });
    }

    public static Process getProcessComplete(){
        Iterator<Process> iterator = processes.values().iterator();
        while (iterator.hasNext()) {
            Process process = iterator.next();
            if (process.getChunkIndex() == process.getTotalChunks() && process.getStatus() == ProcessStatus.RECEIVING) {
                return process;
            }
        }
        return null; // Nenhum processo completo encontrado
    }

    public static void setStatusUploadCompleted(String id){
        executor.submit(() -> {
            processes.computeIfPresent(id, (key, process) ->{
                process.setStatus(ProcessStatus.UPLOAD_COMPLETED);
                return process;
            });
        });
    }

    public static void setImageName(String id, String imageName){
        executor.submit(() -> {
            processes.computeIfPresent(id, (key, process) ->{
                process.setImageName(imageName);
                return process;
            });
        });
    }
}
