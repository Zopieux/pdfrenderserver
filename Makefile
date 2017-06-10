build/libs/pdfrenderserver-1.0.war: src/main/java/eu/zopi/pdfrender/ServerRenderer.java
	gradle war

clean:
	$(RM) -r build/

.PHONY: clean
