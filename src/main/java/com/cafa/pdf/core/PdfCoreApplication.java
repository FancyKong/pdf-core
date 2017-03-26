package com.cafa.pdf.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class PdfCoreApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PdfCoreApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
        /*
         * 为了方便 RequestHolder 获取 request response session application 四大web对象
         * @see cn.cherish.mboot.util.RequestHolder
         */
		servletContext.addListener(new RequestContextListener());
	}

	public static void main(String[] args) {
		SpringApplication.run(PdfCoreApplication.class, args);
	}


}
