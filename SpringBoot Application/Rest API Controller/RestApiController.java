package com.group1.webcrawler.controller;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.group1.webcrawler.controller.service.Lucene;
import com.group1.webcrawler.controller.service.Hadoop.rData;


@RestController
@EnableAsync
class RestApiController {
	rData data =new rData();
	RestApiController(){
        data.loadData("src/main/data/data.dat", "src/main/data/docs.dat", "src/main/data/words.dat");
	}
   @PostMapping(value = "/lucene")
   public String luceneIndexing(@Validated @RequestBody String queryInput) {
	  String result = Lucene.searchIndex(queryInput);
      return result;
   }
   
   @PostMapping(value = "/hadoop")
   public String hadoopIndexing(@Validated @RequestBody String queryInput) {
	   return (String) data.caclulateRank(queryInput);
   }

}
