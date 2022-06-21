/*
https://groovyide.com/cpi/share/v1/rVZtb9s2EP4rrFAgcqFS6PYtsw2saD50a4ogabECUxHQEi1z0QtBUk5dw_99R1GUSIsJFmAKEJO8V94990jHiDW8U9HlMdq0xSG6jJZXNf-j3awzkTUInvN9f9ZJKj4W63e__LpMh7Un561kirXNepmOS0-hYHsm9WmxTMe1p7HpJGuolF3D1HqzTL39mFzqZDduoiTaUVJQIeFepyTiouVUKEb1PromTUFUKw73X0h5_5nU-jiyaSY2G3DyFxENa0pPzxHfCFYTcPMnnVyBhqlHdILAMheM98XN20LLWM1boVDe1lgSjkui6CM5YMZx3gqK806qti7oHneKVfgarktK-lvWDHb_kD0xojtF8gcQBESfmFQgKegWDQ4QFCCH5QeiSGzPavO7QEftRRfztq8ZEuZnZTVwSdV7wEZs5AujrN330Pl2_WnV0Ef0ra5uiIC7xwvM9SIWVn-ymDqxcrzfjKex4722fYLaytVkqS3iLNTGLHLMH03zwsazznqm3DQW-gpyquVzD6Hmj0781L0dlrxi2kEyaruZOuuAZjCx86OAnW0BFaIVAwCkxknfud-FIAe9i_2Enlc0qmmK7pQA5fcdqzRsvBC9lSe3IWZ2ZzHDlsaWbePQrdlPGi9erd4NEY4Tl6Rpn5U5OA2lqCSdKVpAY0MkmJJ8d0T6_72gMKHF2_WkbNGSd-LezPzK0cQXb95cYHOOFf0xFneMtY0ny1eri4sz-dHfwpPvaP5wBSFu-wixEy3xMJZMjpNZyxMlOrp4kXMHlK7rAEySLYG6zr0j_-Tkb3Uv5tcFiHjJY8I5bQoAtkkRMYlqJuGtUCKdEWJFBhCJAtFnRcC8k7v4b-cyWfRV-_j4IYu-hzw4nRWUVySnn4HSj6cXXtWRnhw8GqSjtlM_6gqotxRtuz9g2GDg1a-a1iE7RioN8hGmqB8JS6RyoGnjZIHOZAPJHqB-V7oc9uUAPDErjzYOmw7E6RgHUPBc7C-tIhXyMpChFIZ5fjoT4-gsHxlOyHFm_gRVnWjsGy5rTlagB3o2C0jPuFknA0n0VJi4jKwn41qW_RQgeInW7b6HCPRowPZkaIilx5QipUsqYyDDH1n02mplkbHy8aQRvCdVR1dMzWlm4Bij4FPMmZ85jMHWu8S5fD6wemJ1uFqW06y-niqELhFQCBpv5A4woGFTkeYB3z0wzvWJ2lFkBu6pqR7uPxR9GGmnITZOMhV124pafwWNnPx9fKsEPN_-p3nXj8FTWDY_DTgJU-AT9v_XxV96kzSdtdfO39s1emmnn2tr-DRQt9OMT33zkWPtzPezDh_v267J9fe-_vSfvo7hq_1f
*/

import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.Stack;

import java.util.List;
def Message processData(Message message) {

    Reader reader = message.getBody(Reader)
    def inputXML=new XmlParser().parse(reader)

    def properties=message.getProperties()
    def mandatoryTags=properties.get("Mandatory_Tag_Names")
    def warningTags=properties.get("Warning_Tag_Names")
    def primaryKeyTageName=properties.get("Primary_Key_Tag_Name")
    mandatoryTags=mandatoryTags.split(",")
    warningTags=warningTags.split(",")
    primaryKeyTageName=primaryKeyTageName.split(",")
    

    errorMessagesList=new ArrayList()
    warningMessagesList=new ArrayList()

    // StringBuilder errorMessages=new StringBuilder()
    // StringBuilder warningMessages=new StringBuilder()

    if(primaryKeyTageName.size()!=1)
    {
        //error
    }
    else
    {
        inputXML.FormHeader.each{ each_record->
            def cur_userId=each_record.'**'.userId.text()
            if(cur_userId!='')
            {
               checkEachRecord(each_record,mandatoryTags,cur_userId,errorMessagesList,true)
               checkEachRecord(each_record,warningTags,cur_userId,warningMessagesList,false)
                
            }
            else{
               // errorMessages.append("Record is missing user id\n")
                errorMessagesList.push([cur_userId,"User ID"])
                each_record.replaceNode{}
                
            }
             
        }
    }
  String outxml = groovy.xml.XmlUtil.serialize( inputXML )

message.setBody(outxml) 

message.setProperty("Error Message",errorMessagesList) 
message.setProperty("Warning Message",warningMessagesList) 

message.setProperty("Total Error Messages",errorMessagesList.size()) 
message.setProperty("Total Warning Messages",warningMessagesList.size()) 



return message
}



def  checkEachRecord(  curRecord,  inputArray,  primaryKey,curMsgList,  removeNode)
{
    inputArray.each{each_tag->
        curRecord.'**'."$each_tag".each{
            cur_value=it.text()
         if(cur_value=='')
        {
            
                if(removeNode)
                {
                // cur_msg.append("$primaryKey : Tag $each_tag is missing or blank.Skipping the record\n")
                    curMsgList.push([primaryKey,each_tag,curRecord.formDataId.text()])

                    curRecord.replaceNode{}
                    return
                    
                }
                else{
                    
                    curMsgList.push([primaryKey,each_tag,curRecord.formDataId.text()])
                    return
                    //cur_msg.append("Warning -> $primaryKey : Tag $each_tag is missing or blank.\n")
                    
                    }
                }
        }
        
        
    }

return
}
