package com.example.project.sprintbootuploadfile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import okio.BufferedSink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SprintBootUploadFileApplicationTests {

	private static final int CHUNK_SIZE = 5000;			// Change should be made on both client side and server side
	private static final String FILE_NAME = "image.jpg";
	private static final String GET_INFO_URL = "http://localhost:8090/getInfo";
	private static final String POST_CHUNK_URL = "http://localhost:8090/postChunk";

	@Test
	public void contextLoads() throws IOException {

		// Request Response from server for downloaded data
		String response = requestData();
		System.out.println(response);

		// Map response to FileInfo bean
		ObjectMapper mapper = new ObjectMapper();
		FileInfo file = mapper.readValue(response,FileInfo.class);

		// Post next chunk to server; exit if File completely Uploaded
		if(file.getChunksDownloaded() == file.getTotalChunks()){
			System.out.println("File Completely uploaded!");
		}else {
			response = postData(file.getChunksDownloaded(), file.getTotalChunks(), file.getSize());
		}

		System.out.println(response);


	}

	private String requestData() throws IOException {

		// Create new File Object and get it's meta data
		File file = new File(FILE_NAME);
		long filesize = file.length();
		long chunks;

		if(filesize% CHUNK_SIZE == 0) 	chunks = filesize/ CHUNK_SIZE;
		else							chunks = filesize/ CHUNK_SIZE + 1;

		// Create FileInfo object to hold meta data
		FileInfo fileInfo = new FileInfo(FILE_NAME,filesize, chunks, 0);

		// Map meta data to JSON string
		ObjectMapper mapper = new ObjectMapper();
		String jsonInfo = mapper.writeValueAsString(fileInfo);

		// Send through okHttpClient
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");

		Request request = new Request.Builder().url(GET_INFO_URL).post(RequestBody.create(JSON, jsonInfo)).build();

		OkHttpClient client = new OkHttpClient();

		Response response = client.newCall(request).execute();

		return response.body().string();
	}

	private String postData(long chunksDownloaded, long totalChunks, long fileSize) throws IOException {


		RequestBody requestBody = new RequestBody() {

			@Override
			public MediaType contentType() {
				return MediaType.parse("image/jpg; charset=utf-8");			// TODO : Only sends image/jpg, Fix this
			}

			@Override
			public void writeTo(BufferedSink sink) throws IOException {

				byte[] array = Files.readAllBytes(new File(FILE_NAME).toPath());

				byte[] newArray;

				if(totalChunks-chunksDownloaded == 1){
					newArray = new byte[(int) (fileSize% CHUNK_SIZE)];
				}
				else {
					newArray = new byte[CHUNK_SIZE];
					System.arraycopy(array, (int) (chunksDownloaded * CHUNK_SIZE), newArray, 0, CHUNK_SIZE); // Todo : Fix problem if file size more than range of int
				}

				sink.write(newArray);
			}

		};

		Request request = new Request.Builder()
				.url(POST_CHUNK_URL)
				.post(requestBody)
				.header("file_name",FILE_NAME)
				.header("fileSize",Long.toString(fileSize))
				.build();

		OkHttpClient client = new OkHttpClient();

		Response response = client.newCall(request).execute();

		return response.body().string();

	}

}
