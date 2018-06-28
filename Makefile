


define module_package
mvn --projects $(1) clean package -Dmaven.test.skip=true
endef

define module_install
mvn --projects $(1) clean install -Dmaven.test.skip=true
endef


fzc_package:kafka_config_install
	$(call module_package,fzc)


hive_client_package: kafka_config_install
	$(call module_package,hive-client)


kafka_config_install:clean_install
	@echo $(JAVA_HOME)
	$(call module_install,kafka-config)

print:
	@echo $(PATH)


clean_install:
	mvn clean install -Dmaven.test.skip=true
