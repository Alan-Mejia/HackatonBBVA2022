package com.example.demo;

//import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.*;

import com.google.cloud.spring.bigquery.core.BigQueryTemplate;

import com.google.cloud.spring.bigquery.core.WriteApiResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/** Provides REST endpoint allowing you to load data files to BigQuery using Spring Integration. */
@RestController
@CrossOrigin("*")
public class WebController {

    @SuppressWarnings("*")
    @Autowired
    BigQuerySampleConfiguration.BigQueryFileGateway bigQueryFileGateway;

    @Autowired BigQueryTemplate bigQueryTemplate;

//  @Autowired
//  BigQuery bigQuery;

    @Value("${spring.cloud.gcp.bigquery.datasetName}")
    private String datasetName;

    @GetMapping("/")
    public ModelAndView renderIndex(ModelMap map) {
        map.put("datasetName", this.datasetName);
        return new ModelAndView("index.html", map);
    }

    @GetMapping("/write-api-json-upload")
    public ModelAndView renderUploadJson(ModelMap map) {
        map.put("datasetName", this.datasetName);
        return new ModelAndView("upload-json.html", map);
    }

    /**
     * Handles a file upload using {@link BigQueryTemplate}.
     *
     * @param file the JSON file to upload to BigQuery
     * @param tableName name of the table to load data into
     * @return ModelAndView of the response the send back to users
     * @throws IOException if the file is unable to be loaded.
     */
    @PostMapping("/uploadJsonFile")
    public ModelAndView handleJsonFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tableName") String tableName,
            @RequestParam(name = "createTable", required = false) String createDefaultTable)
            throws IOException {
        ListenableFuture<WriteApiResponse> writeApiRes = null;
        if (createDefaultTable != null
                && createDefaultTable.equals("createTable")) { // create the default table
            writeApiRes =
                    this.bigQueryTemplate.writeJsonStream(
                            tableName, file.getInputStream(), getDefaultSchema());
        } else { // we are expecting the table to be already existing
            writeApiRes = this.bigQueryTemplate.writeJsonStream(tableName, file.getInputStream());
        }
        return getWriteApiResponse(writeApiRes, tableName);
    }

    private Schema getDefaultSchema() {
        return Schema.of(
                Field.of("CompanyName", StandardSQLTypeName.STRING),
                Field.of("Description", StandardSQLTypeName.STRING),
                Field.of("SerialNumber", StandardSQLTypeName.NUMERIC),
                Field.of("Leave", StandardSQLTypeName.NUMERIC),
                Field.of("EmpName", StandardSQLTypeName.STRING));
    }

    /**
     * Handles JSON data upload using using {@link BigQueryTemplate}.
     *
     * @param jsonRows the String JSON data to upload to BigQuery
     * @param tableName name of the table to load data into
     * @return ModelAndView of the response the send back to users
     */
    @PostMapping("/uploadJsonText")
    public ModelAndView handleJsonTextUpload(
            @RequestParam("jsonRows") String jsonRows,
            @RequestParam("tableName") String tableName,
            @RequestParam(name = "createTable", required = false) String createDefaultTable) {
        ListenableFuture<WriteApiResponse> writeApiRes = null;
        if (createDefaultTable != null
                && createDefaultTable.equals("createTable")) { // create the default table

            writeApiRes =
                    this.bigQueryTemplate.writeJsonStream(
                            tableName, new ByteArrayInputStream(jsonRows.getBytes()), getDefaultSchema());
        } else { // we are expecting the table to be already existing
            writeApiRes =
                    this.bigQueryTemplate.writeJsonStream(
                            tableName, new ByteArrayInputStream(jsonRows.getBytes()));
        }
        return getWriteApiResponse(writeApiRes, tableName);
    }

    private ModelAndView getWriteApiResponse(
            ListenableFuture<WriteApiResponse> writeApiFuture, String tableName) {
        String message = null;
        try {
            WriteApiResponse apiResponse = writeApiFuture.get();
            if (apiResponse.isSuccessful()) {
                message = "Successfully loaded data to " + tableName;
            } else if (apiResponse.getErrors() != null && apiResponse.getErrors().size() > 0) {
                message =
                        String.format(
                                "Error occurred while loading the file, printing first error %s. Use WriteApiResponse.getErrors() to get the complete list of errors",
                                apiResponse.getErrors().get(0).getErrorMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "Error: " + e.getMessage();
        }
        return new ModelAndView("upload-json.html")
                .addObject("datasetName", this.datasetName)
                .addObject("message", message);
    }

    /**
     * Handles a file upload using {@link BigQueryTemplate}.
     *
     * @param file the CSV file to upload to BigQuery
     * @param tableName name of the table to load data into
     * @return ModelAndView of the response the send back to users
     * @throws IOException if the file is unable to be loaded.
     */
    @PostMapping("/uploadFile")
    public ModelAndView handleFileUpload(
            @RequestParam("file") MultipartFile file, @RequestParam("tableName") String tableName)
            throws IOException {

        ListenableFuture<Job> loadJob =
                this.bigQueryTemplate.writeDataToTable(
                        tableName, file.getInputStream(), FormatOptions.csv());

        return getResponse(loadJob, tableName);
    }

    /**
     * Handles CSV data upload using Spring Integration {@link BigQuerySampleConfiguration.BigQueryFileGateway}.
     *
     * @param csvData the String CSV data to upload to BigQuery
     * @param tableName name of the table to load data into
     * @return ModelAndView of the response the send back to users
     */
    @PostMapping("/uploadCsvText")
    public ModelAndView handleCsvTextUpload(
            @RequestParam("csvText") String csvData, @RequestParam("tableName") String tableName) {

        ListenableFuture<Job> loadJob =
                this.bigQueryFileGateway.writeToBigQueryTable(csvData.getBytes(), tableName);

        return getResponse(loadJob, tableName);
    }

    @GetMapping("/getAllAtms")
    public List<Map<String, Object>> listOfTableData() throws Exception{
        BigQuery bigQuery1 = BigQueryOptions.newBuilder().setProjectId("bbva-latam-hack22mex-5011").build().getService();

        final String GET_QUERY = "SELECT ATM, Longitud, Latitud, Division FROM `bbva-latam-hack22mex-5011.ATMS.ATM_CLEAN` LIMIT 10";

        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.newBuilder(GET_QUERY).build();
        Job queryJob = bigQuery1.create(JobInfo.newBuilder(queryJobConfiguration).build());
        queryJob = queryJob.waitFor();
        TableResult result = queryJob.getQueryResults();
        List<Map<String, Object>> allRowsOfData = new ArrayList<>();
        for(FieldValueList row : result.iterateAll()){
            Map<String, Object> oneRow = new HashMap<>();

            oneRow.put("atmId", row.get("ATM").getValue());
//      oneRow.put("sitio", row.get("Sitio").getValue());
//      oneRow.put("cr", row.get("CR").getValue());
            oneRow.put("division", row.get("Division").getValue());
//      oneRow.put("marca", row.get("Marca").getValue());
//      oneRow.put("tipoDispositivo", row.get("Tipo_dispositivo").getValue());
//      oneRow.put("estatusDispositivo", row.get("Estatus_dispositivo").getValue());
//      oneRow.put("calle", row.get("Calle").getValue());
//      oneRow.put("numeroExterior", row.get("Num__Ext_").getValue());
//      oneRow.put("estado", row.get("Estado").getValue());
//      oneRow.put("ciudad", row.get("Ciudad").getValue());
//      oneRow.put("cp", row.get("CP").getValue());
//      oneRow.put("delegacionOmunicipio", row.get("Del_Muni").getValue());
//      oneRow.put("colonia", row.get("Colonia").getValue());
            oneRow.put("latitud", Float.valueOf(row.get("Latitud").getValue().toString()));
            oneRow.put("longitud", Float.valueOf(row.get("Longitud").getValue().toString()));
//      oneRow.put("tipoDeLocalidad", row.get("Tipo_localidad").getValue());
//      oneRow.put("idc", row.get("IDC").getValue());
//      oneRow.put("etv", row.get("ETV").getValue());
            allRowsOfData.add(oneRow);
        }
        return allRowsOfData;
    }


    @GetMapping("/getAllAvailable")
    public List<Map<String, Object>> obtenerTodosLosCajerosDisponibles() throws Exception{
        BigQuery bigQuery1 = BigQueryOptions.newBuilder().setProjectId("bbva-latam-hack22mex-5011").build().getService();
        final String GET_QUERY = "SELECT ATM, Sitio, CR, Division, Marca, Estado, Ciudad, Calle, CP, Colonia, CR FROM `bbva-latam-hack22mex-5011.ATMS.ATMSLIST` LIMIT 100";
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.newBuilder(GET_QUERY).build();
        Job queryJob = bigQuery1.create(JobInfo.newBuilder(queryJobConfiguration).build());
        queryJob = queryJob.waitFor();
        TableResult result = queryJob.getQueryResults();
        List<Map<String, Object>> allRowsOfData = new ArrayList<>();
        for(FieldValueList row : result.iterateAll()){
            Map<String, Object> oneRow = new HashMap<>();

            oneRow.put("atmId", row.get("ATM").getValue());
             oneRow.put("sitio", row.get("Sitio").getValue());
            oneRow.put("cr", row.get("CR").getValue());
            oneRow.put("division", row.get("Division").getValue());
            oneRow.put("marca", row.get("Marca").getValue());
//          oneRow.put("tipoDispositivo", row.get("Tipo_dispositivo").getValue());
//          oneRow.put("estatusDispositivo", row.get("Estatus_dispositivo").getValue());
          oneRow.put("calle", row.get("Calle").getValue());
//          oneRow.put("numeroExterior", row.get("Num__Ext_").getValue());
          oneRow.put("estado", row.get("Estado").getValue());
          oneRow.put("ciudad", row.get("Ciudad").getValue());
          oneRow.put("cp", row.get("CP").getValue());
//          oneRow.put("delegacionOmunicipio", row.get("Del_Muni").getValue());
            oneRow.put("colonia", row.get("Colonia").getValue());
//            oneRow.put("latitud", Float.valueOf(row.get("Latitud").getValue().toString()));
//            oneRow.put("longitud", Float.valueOf(row.get("Longitud").getValue().toString()));
//          oneRow.put("tipoDeLocalidad", row.get("Tipo_localidad").getValue());
//          oneRow.put("idc", row.get("IDC").getValue());
//          oneRow.put("etv", row.get("ETV").getValue());
            allRowsOfData.add(oneRow);
        }
        return allRowsOfData;
    }

    @GetMapping("/fallasMasComunes")
    public List<Map<String, Object>> fallasMasComunes() throws Exception{
        BigQuery bigQuery1 = BigQueryOptions.newBuilder().setProjectId("bbva-latam-hack22mex-5011").build().getService();
        final String GET_QUERY = "SELECT \n" +
                "  FALLA,\n" +
                "  COUNT(ATM_ID) AS NUMERO_DE_FALLAS,\n" +
                "FROM `bbva-latam-hack22mex-5011.ATMS.issues_atmm_vip_buenon` \n" +
                "GROUP BY\n" +
                "  FALLA\n" +
                "ORDER BY\n" +
                "  NUMERO_DE_FALLAS DESC\n";
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.newBuilder(GET_QUERY).build();
        Job queryJob = bigQuery1.create(JobInfo.newBuilder(queryJobConfiguration).build());
        queryJob = queryJob.waitFor();
        TableResult result = queryJob.getQueryResults();
        List<Map<String, Object>> allRowsOfData = new ArrayList<>();
        for(FieldValueList row : result.iterateAll()){
            Map<String, Object> oneRow = new HashMap<>();
            oneRow.put("tipoFalla", row.get("FALLA").getValue());
            oneRow.put("numeroDeFallas", row.get("NUMERO_DE_FALLAS").getValue());
            allRowsOfData.add(oneRow);
        }
        return allRowsOfData;
    }

    @GetMapping("/macaConMasFallas")
    public List<Map<String, Object>> marcaDeCajeroConMasFallas() throws Exception{
        BigQuery bigQuery1 = BigQueryOptions.newBuilder().setProjectId("bbva-latam-hack22mex-5011").build().getService();
        final String GET_QUERY= "SELECT \n" +
                "  Marca,\n" +
                "  COUNT(ATM_ID) AS NUMERO_DE_FALLAS,\n" +
                "FROM `bbva-latam-hack22mex-5011.ATMS.issues_atmm_vip_buenon` \n" +
                "GROUP BY\n" +
                "  Marca\n" +
                "ORDER BY\n" +
                "  NUMERO_DE_FALLAS DESC";
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.newBuilder(GET_QUERY).build();
        Job queryJob = bigQuery1.create(JobInfo.newBuilder(queryJobConfiguration).build());
        queryJob = queryJob.waitFor();
        TableResult result = queryJob.getQueryResults();
        List<Map<String, Object>> allRowsOfData = new ArrayList<>();
        for(FieldValueList row : result.iterateAll()){
            Map<String, Object> oneRow = new HashMap<>();
            oneRow.put("marca", row.get("Marca").getValue());
            oneRow.put("numeroDeFallas", row.get("NUMERO_DE_FALLAS").getValue());
            allRowsOfData.add(oneRow);
        }
        return allRowsOfData;
    }

    private ModelAndView getResponse(ListenableFuture<Job> loadJob, String tableName) {
        String message;
        try {
            Job job = loadJob.get();
            message = "Successfully loaded data file to " + tableName;
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error: " + e.getMessage();
        }

        return new ModelAndView("index")
                .addObject("datasetName", this.datasetName)
                .addObject("message", message);
    }
}
