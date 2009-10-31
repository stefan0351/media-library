<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style type="text/css">
	table#languageTable .name {
		width: 150px
	}

	table#languageTable .symbol {
		width: 50px
	}

	table#countryTable .name {
		width: 150px
	}

	table#countryTable .symbol {
		width: 50px
	}

	input.imdbKey {
		width: 80px
	}
</style>
<script type="text/javascript">
	function updateRows(table, rowIndex)
	{
		for (var i=rowIndex; i<table.tBodies[0].rows.length; i++)
		{
			var row=table.tBodies[0].rows[i];
			if (i%2==0)
			{
				row.removeClassName("even");
				row.addClassName("odd");
			}
			else
			{
				row.removeClassName("odd");
				row.addClassName("even");
			}
		}
		if (table.id=="castTable")
		{
			for (i=rowIndex;i<table.tBodies[0].rows.length;i++)
			{
				table.tBodies[0].rows[i].cells[0].innerHTML=i+1;								
			}
		}
	}
	function addLanguageOrCountry(element)
	{
		var table=$(element).up("table");
		var rowIndex=table.tBodies[0].rows.length;
		var row=table.tBodies[0].insertRow(rowIndex);
		var cell=row.insertCell(0);
		cell.innerHTML="<input class=\"name\"/>";
		cell=row.insertCell(1);
		cell.innerHTML="<input class=\"symbol\"/>";
		cell=row.insertCell(2);
		cell.innerHTML="<img src=\"<media:url icon="delete"/>\" alt=\"[Remove]\" onclick=\"removeElement(this);\"/>";
		updateRows(table, rowIndex);
	}
	function addCast(element)
	{
		var table=$(element).up("table");
		var rowIndex=table.tBodies[0].rows.length;
		var row=table.tBodies[0].insertRow(rowIndex);
		var cell=row.insertCell(0);
		cell.innerHTML=rowIndex+1;
		cell=row.insertCell(1);
		cell.innerHTML="<input class=\"actor\"/>";
		cell=row.insertCell(2);
		cell.innerHTML="<input class=\"imdbKey\"/>";
		cell=row.insertCell(3);
		cell.innerHTML="...";
		cell=row.insertCell(4);
		cell.innerHTML="<input class=\"role\"/>";
		cell=row.insertCell(5);
		cell.innerHTML="<img src=\"<media:url icon="delete"/>\" alt=\"[Remove]\" onclick=\"removeElement(this);\"/>"
						+" <img src=\"<media:url icon="move.up"/>\" alt=\"[Move Up]\" onclick=\"moveUp(this);\"/>"
						+" <img src=\"<media:url icon="move.down"/>\" alt=\"[Move Down]\" onclick=\"moveDown(this);\"/>";
		updateRows(table, rowIndex);
	}
	function addCrew(element)
	{
		var table=$(element).up("table");
		var rowIndex=table.tBodies[0].rows.length;
		var row=table.tBodies[0].insertRow(rowIndex);
		var cell=row.insertCell(0);
		cell.innerHTML="<input class=\"name\"/>";
		cell=row.insertCell(1);
		cell.innerHTML="<input class=\"imdbKey\"/>";
		cell=row.insertCell(2);
		cell.innerHTML="...";
		cell=row.insertCell(3);
		cell.innerHTML="<select class=\"type\">"
				+"<option value=\"\" selected>Select a type...</option>"
				<s:iterator value="allCreditTypes">+"<option value=\"<s:property value="id"/>\"><s:property value="asName"/></option>"</s:iterator>
				+"</select>";
		cell=row.insertCell(4);
		cell.innerHTML="<input class=\"subType\"/>";
		cell=row.insertCell(5);
		cell.innerHTML="<img src=\"<media:url icon="delete"/>\" alt=\"[Remove]\" onclick=\"removeElement(this);\"/>";
		updateRows(table, rowIndex);
	}
	function moveUp(element)
	{
		var row=$(element).up("tr");
		var table=row.up("table");
		var index=$A(table.tBodies[0].rows).indexOf(row);
		if (index>0)
		{
			var previousRow=table.tBodies[0].rows[index-1];
			var temp=previousRow.innerHTML;
			previousRow.innerHTML=row.innerHTML;
			row.innerHTML=temp;
			updateRows(table, index-1);
		}
	}
	function moveDown(element)
	{
		var row=$(element).up("tr");
		var table=row.up("table");
		var index=$A(table.tBodies[0].rows).indexOf(row);
		if (index+1<table.tBodies[0].rows.length)
		{
			var nextRow=table.tBodies[0].rows[index+1];
			var temp=nextRow.innerHTML;
			nextRow.innerHTML=row.innerHTML;
			row.innerHTML=temp;
			updateRows(table, index);
		}
	}
	function removeElement(element)
	{
		var row=$(element).up("tr");
		var table=row.up("table");
		row.remove();
		updateRows(table, 0);
	}
	function submitForm()
	{
		var movieTable=$("movieTable");
		var movie=new Object();
		var errors=new Array();
		movie.imdbKey=movieTable.down("input#imdbKey").value;
		movie.title=movieTable.down("input#title").value;
		if (movie.title.empty()) errors.push("Title is empty!");
		movie.germanTitle=movieTable.down("input#germanTitle").value;
		movie.summary=movieTable.down("textarea#summary").value;
		movie.runtime=parseInt(movieTable.down("input#runtime").value);
		if (isNaN(movie.runtime)) errors.push("Runtime must be an integer!");
		movie.year=parseInt(movieTable.down("input#year").value);
		if (isNaN(movie.year)) errors.push("Year must be an integer!");
		var i, row;
		movie.languages=new Array();
		var table=$("languageTable");
		for (i=0; i<table.tBodies[0].rows.length; i++)
		{
			row=table.tBodies[0].rows[i];
			var language=
			{
				name: row.down(".name").value,
				symbol: row.down(".symbol").value
			};
			if (language.name.empty() && language.symbol.empty())
				errors.push("Language has neither name or symbol!");
			movie.languages.push(language);
		}
		movie.countries=new Array();
		table=$("countryTable");
		for (i=0; i<table.tBodies[0].rows.length; i++)
		{
			row=table.tBodies[0].rows[i];
			var country=
			{
				name: row.down(".name").value,
				symbol: row.down(".symbol").value
			};
			if (country.name.empty() && country.symbol.empty())
				errors.push("Country has neither name or symbol!");
			movie.countries.push(country);
		}
		movie.cast=new Array();
		table=$("castTable");
		for (i=0; i<table.tBodies[0].rows.length; i++)
		{
			row=table.tBodies[0].rows[i];
			var castMember=
			{
				creditOrder: i+1,
				actor: row.down(".actor").value,
				imdbKey: row.down(".imdbKey").value,
				role: row.down(".role").value
			};
			if (castMember.actor.empty()) errors.push("Cast member #"+castMember.creditOrder+" has no actor!");
			movie.cast.push(castMember);
		}
		movie.crew=new Array();
		table=$("crewTable");
		for (i=0; i<table.tBodies[0].rows.length; i++)
		{
			row=table.tBodies[0].rows[i];
			var crewMember=
			{
				name: row.down(".name").value,
				imdbKey: row.down(".imdbKey").value,
				typeId: parseInt(row.down(".type").value),
				subType: row.down(".subType").value
			};
			if (crewMember.name.empty()) errors.push("Crew member #"+(i+1)+" has no name!");
			if (isNaN(crewMember.typeId)) errors.push("Crew member #"+(i+1)+" has no type!");
			movie.crew.push(crewMember);
		}
		if (errors.length>0)
		{
			showErrors(errors);
			return;
		}
		var request=
		{
			movieId: parseInt(movieTable.down("select#movie").value),
			movieData: movie
		};
		$("submitMovie").disabled=true;
		new Ajax.Request("<%=request.getContextPath()%>/SaveMovie.action",
		{
			method: "post",
			contentType: "application/json",
			postBody: Object.toJSON(request),
			onSuccess: function(response)
			{
				var data=response.responseJSON;
				if (!data.returnCode)
				{
					$("submitMovie").disabled=false;
					showErrors(data.actionErrors);
				}
				else window.location.href="<s:url action="MovieDetails"/>?movieId="+data.movieId;
			},
			onFailure: function(response)
			{
				$("submitMovie").disabled=false;
				showError(response.responseText);
			},
			onException: function(request, exception)
			{
				$("submitMovie").disabled=false;
				alert("onException: "+exception.message);
			}
		});
	}
</script>

<media:panel title="Import from IMDb">
	<table id="movieTable" border=0 cellspacing="5">
		<tr class="content"><td><b>Update Movie:</b></td><td>
			<select id="movie" style="width:500px">
				<option value="" <s:if test="movies.empty">selected</s:if>>New Movie</option>
				<s:iterator value="movies" status="it">
					<option value="<s:property value="id"/>" <s:if test="#it.first">selected</s:if> ><s:property value="title"/> <s:if test="year!=null">(<s:property value="year"/></s:if>)</option>
				</s:iterator>
			</select>
		</td></tr>
		<tr class="content"><td><b>IMDb Key:</b></td><td><input id="imdbKey" class="imdbKey" value="<s:property value="movieData.imdbKey"/>" readonly/></td></tr>
		<tr class="content"><td><b>Title:</b></td><td><input id="title" value="<s:property value="movieData.title"/>" style="width:500px"/></td></tr>
		<tr class="content"><td><b>German Title:</b></td><td><input id="germanTitle" value="<s:property value="movieData.germanTitle"/>" style="width:500px"/>
		</td></tr>
		<tr class="content" valign="top"><td><b>Summary:</b></td><td><textarea id="summary" style="width:500px;" rows="8" cols="80"><s:property
				value="movieData.summary"/></textarea></td></tr>
		<tr class="content"><td><b>Runtime:</b></td><td><input id="runtime" value="<s:property value="movieData.runtime"/>" class="numberField"/> min</td>
		</tr>
		<tr class="content"><td><b>Year:</b></td><td><input id="year" value="<s:property value="movieData.year"/>" class="numberField"/></td></tr>
		<tr class="content" valign="top"><td><b>Language:</b></td>
			<td>
				<table id="languageTable" class="stdTable">
					<thead>
					<tr><td>Name</td><td>Symbol</td><td></td></tr>
					</thead>
					<tbody>
					<s:iterator value="movieData.languages" status="it">
						<tr class="<s:if test="#it.even">even</s:if><s:else>odd</s:else>">
							<td><input class="name" value="<s:property value="name"/>"/></td>
							<td><input class="symbol" value="<s:property value="symbol"/>"/></td>
							<td><img src="<media:url icon="delete"/>" alt="[Remove]" onclick="removeElement(this);"/></td>
						</tr>
					</s:iterator>
					</tbody>
					<tfoot>
					<tr><td colspan="2"></td><td><img src="<media:url icon="add"/>" alt="[Add]" onclick="addLanguageOrCountry(this);"/></td>
					</tr>
					</tfoot>
				</table>
			</td>
		</tr>
		<tr class="content" valign="top"><td><b>Country:</b></td>
			<td>
				<table id="countryTable" class="stdTable">
					<thead>
					<tr><td>Name</td><td>Symbol</td><td></td></tr>
					</thead>
					<tbody>
					<s:iterator value="movieData.countries" status="it">
						<tr class="<s:if test="#it.even">even</s:if><s:else>odd</s:else>">
							<td><input class="name" value="<s:property value="name"/>"></td>
							<td><input class="symbol" value="<s:property value="symbol"/>"></td>
							<td><img src="<media:url icon="delete"/>" alt="[Remove]" onclick="removeElement(this);"/></td>
						</tr>
					</s:iterator>
					</tbody>
					<tfoot>
					<tr><td colspan="2"></td><td><img src="<media:url icon="add"/>" alt="[Add]" onclick="addLanguageOrCountry(this);"/></td>
					</tr>
					</tfoot>
				</table>
			</td>
		</tr>
		<tr class="content" valign="top"><td><b>Cast:</b></td>
			<td>
				<table id="castTable" class="stdTable">
					<thead>
					<tr><td>#</td><td>Actor</td><td>IMDb Key</td><td></td><td>Role</td><td></td></tr>
					</thead>
					<tbody>
					<s:iterator value="movieData.cast" status="it">
						<tr class="<s:if test="#it.even">even</s:if><s:else>odd</s:else>">
							<td><s:property value="#it.count"/></td>
							<td><input class="actor" value="<s:property value="actor"/>"/></td>
							<td><input class="imdbKey" value="<s:property value="imdbKey"/>"/></td>
							<td>...</td>
							<td><input class="role" value="<s:property value="role"/>"/></td>
							<td><img src="<media:url icon="delete"/>" alt="[Remove]" onclick="removeElement(this);"/>
								<img src="<media:url icon="move.up"/>" alt="[Move Up]" onclick="moveUp(this);"/>
								<img src="<media:url icon="move.down"/>" alt="[Move Down]" onclick="moveDown(this);"/></td>
						</tr>
					</s:iterator>
					</tbody>
					<tfoot>
					<tr><td colspan="5"></td><td><img src="<media:url icon="add"/>" alt="[Add]" onclick="addCast(this);"/></td></tr>
					</tfoot>
				</table>
			</td>
		</tr>
		<tr class="content" valign="top"><td><b>Crew:</b></td>
			<td>
				<table id="crewTable" class="stdTable">
					<thead>
					<tr><td>Name</td><td>IMDb Key</td><td></td><td>Type</td><td>Subtype</td><td></td></tr>
					</thead>
					<tbody>
					<s:iterator value="movieData.crew" status="it">
						<tr class="<s:if test="#it.even">even</s:if><s:else>odd</s:else>">
							<td><input class="name" value="<s:property value="name"/>"/></td>
							<td><input class="imdbKey" value="<s:property value="imdbKey"/>"/></td>
							<td>...</td>
							<td><select class="type">
								<option value="">Select a type...</option>
								<s:iterator value="allCreditTypes">
									<option value="<s:property value="id"/>" <s:if test="id==typeId">selected</s:if>><s:property value="asName"/></option>
								</s:iterator>
							</select></td>
							<td><input class="subType" value="<s:property value="subType"/>"/></td>
							<td><img src="<media:url icon="delete"/>" alt="[Remove]" onclick="removeElement(this);"/></td>
						</tr>
					</s:iterator>
					</tbody>
					<tfoot>
					<tr><td colspan="5"></td><td><img src="<media:url icon="add"/>" alt="[Add]" onclick="addCrew(this);"/></td></tr>
					</tfoot>
				</table>
			</td>
		</tr>
		<tr class="content"><td colspan="2" align="right"><button id="submitMovie" type="button" onclick="submitForm();">Submit</button></td></tr>
	</table>
</media:panel>
