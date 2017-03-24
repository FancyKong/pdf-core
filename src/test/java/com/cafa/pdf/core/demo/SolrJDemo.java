/**
 * JDKCC.com
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.cafa.pdf.core.demo;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

/**
 * @author Jiangjiaze
 * @version Id: SolrJDemo.java, v 0.1 2016/12/3 18:28 FancyKong Exp $$
 */
public class SolrJDemo {
    public static void main(String[] args) throws IOException, SolrServerException {
        String urlString = "http://114.215.140.181:8983/solr/pdfCore";
        SolrClient solr = new HttpSolrClient(urlString);
        SolrQuery query = new SolrQuery();
        query.setQuery("features:敬爱的党组织");
        //query.set("fl", "author");
        //query.setFields("category", "title", "price");
        //query.set("q", "category:books");
        //query.setRequestHandler("/spellCheckCompRH");
        QueryResponse response = solr.query(query);
        SolrDocumentList list = response.getResults();
        System.out.println();
        System.out.println(list);
    }
}
