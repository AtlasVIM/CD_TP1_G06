package svcserver;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

    public static List<Process> processes = new ArrayList<>();

    public static boolean processExists(String id){

        if (processes.isEmpty())
            return false;

        for (Process process: processes){
            if (process.getId().equals(id)){
                return true;
            }
        }

        return false;
    }

    public static void addNewProcess(String id, int totalChunks){
        processes.add(new Process(id, totalChunks));
    }

    public static Process getProcess(String id){
        for (Process process: processes){
            if (process.getId().equals(id)){
                return process;
            }
        }

        return null;
    }

    public static void setUploadRequestObject(String id, ByteArrayOutputStream uploadRequestObject){
        for (Process process: processes){
            if (process.getId().equals(id)){
                process.setUploadRequestObject(uploadRequestObject);
            }
        }
    }

    public static void setChunks(String id, int chunkIndex){
        for (Process process: processes){
            if (process.getId().equals(id)){
                process.setChunkIndex(chunkIndex);
            }
        }
    }

    public static Process getProcessComplete(){
        for (Process process: processes){
            if (process.getChunkIndex() == process.getTotalChunks() && process.getStatus() == ProcessStatus.RECEIVING){
                return process;
            }
        }

        return null;
    }

    public static void setStatusUploadCompleted(String id){
        for (Process process: processes){
            if (process.getId().equals(id)){
                process.setStatus(ProcessStatus.UPLOAD_COMPLETED);
            }
        }
    }

    public static void setImageName(String id, String imageName){
        for (Process process: processes){
            if (process.getId().equals(id)){
                process.setImageName(imageName);
            }
        }
    }
}
