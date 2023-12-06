package traitementData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import saveData.Data;
import saveData.DixData;

public class test {
	public static void main(String[] args) {
		List<DixData> bigData = new ArrayList<>();
		Map<String,List<Data>> datas = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		bigData.addAll(Arrays.stream(Objects.requireNonNull(new File("datas10").listFiles())).map(file -> {
			try {
				return objectMapper.readValue(file, DixData.class);
			} catch (IOException e) {
				e.printStackTrace();
			} return null;
		}).toList());
		System.out.println(bigData.size());
		bigData.forEach(bd -> {
			System.out.println(bd.datas()[0]);

//			bd.datas().forEach(data -> 
//				datas.getOrDefault(data.goodWord(), new ArrayList<Data>()).add(data)
//			);
		});
	}
}
