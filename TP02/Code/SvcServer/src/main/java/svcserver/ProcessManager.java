package svcserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ProcessManager {

    private static final ConcurrentHashMap<String, Process> processes = new ConcurrentHashMap<>(); //Gerenciar a concorrencia
    private static final Semaphore semaphore = new Semaphore(1);

    public static void addNewProcess(String id, String imageName){
        processes.putIfAbsent(id, new Process(id, imageName));
    }

    public static void addNewProcess(String id, int totalChunks){
        processes.putIfAbsent(id, new Process(id, totalChunks));
    }

    public static Process getProcess(String id){
        return processes.get(id);
    }

    public static void setChunkUploadRequestObject(String id, byte[] chunk){
        processes.computeIfPresent(id, (key, process) -> {
            synchronized (process) {
                try {
                    ByteArrayOutputStream outputStream = process.getUploadRequestObject();
                    outputStream.write(chunk);
                    return process;
                } catch (IOException e) {
                    System.out.println("An unexpected error occur when setChunkUploadRequestObject");
                    e.printStackTrace();
                    return process;
                }
            }
        });
    }

    public static void setChunks(String id, int chunkIndex){
        processes.computeIfPresent(id, (key, process) -> {
            process.setChunkIndex(chunkIndex);
            return process;
        });
    }


    public static void setStatusUploadCompleted(String id){
        processes.computeIfPresent(id, (key, process) -> {
            process.setStatus(ProcessStatus.UPLOAD_COMPLETED);
            return process;
        });
    }

    public static void setImageName(String id, String imageName){
        processes.computeIfPresent(id, (key, process) -> {
            process.setImageName(imageName);
            return process;
        });
    }

    public static List<Process> getAllProcesses() {
        List<Process> processList = new ArrayList<>();
        processList.addAll(processes.values());
        return processList;
    }

    public static void updateProcesses(List<Process> newProcesses){
        try {
            semaphore.acquire();
            for (Process process : newProcesses) {
                processes.put(process.getId(), process);
            }
        } catch (InterruptedException e) {
            System.out.println("An unexpected error occur when updateProcesses");
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void setProcessCompleted(String id, String imageNameMarks){
        processes.computeIfPresent(id, (key, process) -> {
            process.setImageNameMarks(imageNameMarks);
            process.setStatus(ProcessStatus.PROCESSED);
            return process;
        });
    }
}
