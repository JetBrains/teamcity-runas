/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.runAs.agent;

import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.Nullable;

public class CryptographicServiceImpl implements CryptographicService {
  @Nullable
  public String unscramble(@Nullable String str){
    if(StringUtil.isEmptyOrSpaces(str)) {
      return str;
    }

    if(!EncryptUtil.isScrambled(str))
    {
      return str;
    }

    return EncryptUtil.unscramble(str);
  }
}