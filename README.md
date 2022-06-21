# CPI_SF_Reuasble_Scripts
Collection of  some commonly used scripts which can be re-used to integrate SF EC with other 3rd party systems

Script 1: GV_Create_Odata_Filter_condition

        This groovy script can be used to create the filter condition for the SF Odata APIs. It can be used to extract History,Current(future become effective can also be selected) and Future dated records.The properties are included using which the filter can be generated for various scenarios.
 
Script 2: GV_Check_Mandatory_Fields 

        This groovy script can be used to check if the mandatory fields are present in the input xml body. There are 3 properties which are being used here
                1) Primary Key  - Field which uniquely identifies the record 
                2) Mandatory_Tag_Names - Feilds which are required. If these fields are not available/blank in the input then the groovy will remove the parent node and they are added to the list which can be used later for reporting.
                3) Warning_Tag_Names - Feilds which are not mandatory.If these fields are not available/blank in the input then it is added to list which can be         later used for reoprting the warning messages.
