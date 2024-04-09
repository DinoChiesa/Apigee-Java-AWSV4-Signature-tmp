// Copyright (c) 2016 Apigee Corp, 2017-2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// @author: Dino Chiesa

package com.google.apigee.callouts;

import com.apigee.flow.execution.ExecutionResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AWSV4Signature_Tests3 extends TestBase {

  @Override
  boolean isVerbose() {
    return true;
  }

  // @Override
  // String testDataDir() {
  //   return "src/test/resources/other";
  // }

  @Test()
  public void test1() throws IOException {
    final String testName = "test1";
    final String testDataDir = "src/test/resources/other";
    TestCase tc = new TestCase("post-textract", Paths.get(testDataDir, "post-textract"));

    final String creq = tc.canonicalRequest();
    final String sts = tc.stringToSign();
    final String authz = tc.authorization();

    System.out.printf("%s\n", testName);
    msgCtxt.setVariable("source", message);
    message.setVariable("verb", "POST");
    message.setVariable("path", "/");
    message.setHeader("x-amz-date", "20240405T174302Z");
    message.setHeader("host", "textract.us-east-1.amazonaws.com");
    message.setHeader("content-type", "application/x-amz-json-1.1");
    String content =
        TestCase.readAll(Paths.get(testDataDir, "post-textract", "textract-body-1.json")).trim();
    message.setContent(content);

    message.setHeader("content-length", String.valueOf(content.length()));

    Properties props = new Properties();
    // props.setProperty("debug", "true");
    props.setProperty("debug", "true");
    props.setProperty("sign-content-sha256", "true");
    props.setProperty("source", "source");
    props.setProperty("key", "AKIATYKL3VV5HR64ZUWM");
    props.setProperty("secret", "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");
    props.setProperty("region", "us-east-1");
    props.setProperty("service", "textract");
    props.setProperty("endpoint", "https://" + message.getHeader("host"));

    AWSV4Signature callout = new AWSV4Signature(props);

    // execute and retrieve output
    ExecutionResult actualResult = callout.execute(msgCtxt, exeCtxt);
    ExecutionResult expectedResult = ExecutionResult.SUCCESS;

    // check result and output
    Assert.assertEquals(actualResult, expectedResult, testName + " result not as expected");
    Assert.assertNull(msgCtxt.getVariable("awsv4sig_error"), testName);
    Assert.assertEquals(xform("creq"), creq, testName);
    Assert.assertEquals(xform("sts"), sts, testName);
    Assert.assertEquals(message.getHeader("authorization"), authz, testName);
  }
}
