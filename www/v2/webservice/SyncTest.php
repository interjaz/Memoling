<?php 
require_once("../Init.php");

class SyncTest {

	public function JustClientWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$engine->resolveEngine($clientActions, $serverActions);
		$expServer = $clientActions;
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function JustMultiClientWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$engine->resolveEngine($clientActions, $serverActions);
		$expServer = $clientActions;
	
		return $this->isPassed($engine, $expClient, $expServer);
	}

	public function JustServerWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
		$expClient = $serverActions;
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function JustMultiServerWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
		$expClient = $serverActions;
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function InsertInsertedSameTime() {
		$engine = new SyncResolver();
		
		$clientActions = array();
		$serverActions = array();
		
		$expServer = array();
		$expClient = array();

		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;

		$engine->resolveEngine($clientActions, $serverActions);
		
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function InsertInsertedDiffTimeServer() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function InsertInsertedDiffTimeClient() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function SimpleClientWin() {
		$engine = new SyncResolver();
		
		$clientActions = array();
		$serverActions = array();
		
		$expServer = array();
		$expClient = array();

		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;

		$engine->resolveEngine($clientActions, $serverActions);
		$expServer[] = $ca;
		
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function SimpleServerWin() {
		$engine = new SyncResolver();
		
		$clientActions = array();
		$serverActions = array();
		
		$expServer = array();
		$expClient = array();

		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C6"}');
		$serverActions[] = $sa;
		
		$engine->resolveEngine($clientActions, $serverActions);
		$expClient[] = $sa;

		return $this->isPassed($engine, $expClient, $expServer);
	}

	public function MutualExWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;

		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $serverActions, $clientActions);
	}
	
	public function DeleteServerWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C2","extra":"C6"}');
		$serverActions[] = $sa;
		$expClient[] = $sa;
		
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function DeleteMultiServerWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C2","extra":"C6"}');
		$serverActions[] = $sa;
		$expClient[] = $sa;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function DeleteClientWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		$expServer[] = $ca;
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function DeleteMultiClientWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		$expServer[] = $ca;
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	
	public function DeleteClientServerConflictWin() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		$expServer[] = $ca;
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C2","extra":"C6"}');
		$serverActions[] = $sa;
		$expClient[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function DeleteClientServerConflictWin2() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		$expServer[] = $ca;
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C2","extra":"C5"}');
		$clientActions[] = $ca;
		$expServer[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C3","extra":"C6"}');
		$serverActions[] = $sa;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T1","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
		$expClient[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}

	public function DeleteTimeConflict() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$engine->resolveEngine($clientActions, $serverActions);
	
		return $this->isPassed($engine, $expClient, $expServer);
	}

	public function DeleteServerLose() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$insertSa = new SyncAction();
		$insertSa->decode('{"table":"T2","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$expServer[] = $insertSa;
		
		$engine->resolveEngine($clientActions, $serverActions);
		return $this->isPassed($engine, $expClient, $expServer);
	}

	public function DeleteClientLose() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$insertCa = new SyncAction();
		$insertCa->decode('{"table":"T2","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$expClient[] = $insertCa;
		
		$engine->resolveEngine($clientActions, $serverActions);
		return $this->isPassed($engine, $expClient, $expServer);
	}
	


	public function DeleteServerMultiLose() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
		
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
	
		$insertSa = new SyncAction();
		$insertSa->decode('{"table":"T2","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C1","extra":"C5"}');
		$expServer[] = $insertSa;
	
		$engine->resolveEngine($clientActions, $serverActions);
		return $this->isPassed($engine, $expClient, $expServer);
	}
	
	public function DeleteClientMultiLose() {
		$engine = new SyncResolver();
	
		$clientActions = array();
		$serverActions = array();
	
		$expServer = array();
		$expClient = array();
	
		$ca = new SyncAction();
		$ca->decode('{"table":"T2","primaryKey":"1","action":-1,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C1","extra":"C5"}');
		$clientActions[] = $ca;
	
		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;

		$sa = new SyncAction();
		$sa->decode('{"table":"T2","primaryKey":"1","action":0,"serverTimestamp":'. strtotime("2014-01-01T00:00:00Z") .',"updateColumn":"C4","extra":"C6"}');
		$serverActions[] = $sa;
		
		$insertCa = new SyncAction();
		$insertCa->decode('{"table":"T2","primaryKey":"1","action":1,"serverTimestamp":'. strtotime("2014-01-01T00:00:01Z") .',"updateColumn":"C4","extra":"C6"}');
		$expClient[] = $insertCa;
	
		$engine->resolveEngine($clientActions, $serverActions);
		return $this->isPassed($engine, $expClient, $expServer);
	}

	private function isPassed($engine, $expectedPendingClient, $expectedPendingServer) {
		
		$passed = ($engine->PendingClientActions == $expectedPendingClient &&
				   $engine->PendingServerActions == $expectedPendingServer);
		
		if(!$passed) {
			echo '<table style="font-size: 0.7em">';
			echo '<tr><td>Client:</td></tr>';
			echo '<tr><td><pre>'; var_dump($engine->PendingClientActions); echo '</pre></td>';
			echo '<td><pre>'; var_dump($expectedPendingClient); echo '</pre></td></tr>';
			echo '<tr><td>Server:</td></tr>';
			echo '<tr><td><pre>'; var_dump($engine->PendingServerActions); echo '</pre></td>';
			echo '<td><pre>'; var_dump($expectedPendingServer); echo '</pre></td></tr>';
			echo '</table>';
		}
		
		return $passed ? "<span style='color:green'>Passed</span>" : "<span style='color:red'>Not Passed</span>";
	}
	
}

class RefTest {
	
	/** @DB\Column(Name="asd\"test",Dog=sdf) **/
	public $name;
	
	public function Read() {
		$reflection = new ReflectionClass($this);
		$properties = $reflection->getProperties();
		
		foreach($properties as $property) {
			$doc = $property->getDocComment();
			echo $doc;
			var_dump(strpos($doc, "@DB\Column("));
			var_dump(strpos($doc, ")",4));
		}
		
	}
}


$test = new SyncTest();
echo '<table>';
echo '<tr><td>Just Client Win:</td><td>' . $test->JustClientWin() . "</td></tr>";
echo '<tr><td>Just Multi Client Win:</td><td>' . $test->JustMultiClientWin() . "</td></tr>";
echo '<tr><td>Just Server Win:</td><td>' . $test->JustServerWin() . "</td></tr>";
echo '<tr><td>Just Multi Server Win:</td><td>' . $test->JustMultiServerWin() . "</td></tr>";
echo '<tr><td>Insert Inserted Same Time:</td><td>' . $test->InsertInsertedSameTime() . "</td></tr>";
echo '<tr><td>Insert Inserted Diff Time Server:</td><td>' . $test->InsertInsertedDiffTimeServer() . "</td></tr>";
echo '<tr><td>Insert Inserted Diff Time Client:</td><td>' . $test->InsertInsertedDiffTimeClient() . "</td></tr>";
echo '<tr><td>Simple Client Win:</td><td>' . $test->SimpleClientWin() . "</td></tr>";
echo '<tr><td>Simple Server Win:</td><td>' . $test->SimpleServerWin() . "</td></tr>";
echo '<tr><td>Mutaul Ex Win:</td><td>' . $test->MutualExWin() . "</td></tr>";
echo '<tr><td>Delete Server Win:</td><td>' . $test->DeleteServerWin() . "</td></tr>";
echo '<tr><td>Delete Multi Server Win:</td><td>' . $test->DeleteMultiServerWin() . "</td></tr>";
echo '<tr><td>Delete Client Win:</td><td>' . $test->DeleteClientWin() . "</td></tr>";
echo '<tr><td>Delete Multi Client Win:</td><td>' . $test->DeleteMultiClientWin() . "</td></tr>";
echo '<tr><td>Delete Client ServerConflict Win:</td><td>' . $test->DeleteClientServerConflictWin() . "</td></tr>";
echo '<tr><td>Delete Client ServerConflict Win2:</td><td>' . $test->DeleteClientServerConflictWin2() . "</td></tr>";
echo '<tr><td>Delete Time Conflict</td><td>' . $test->DeleteTimeConflict() . "</td></tr>";
echo '<tr><td>Delete Server Lose</td><td>' . $test->DeleteServerLose() . "</td></tr>";
echo '<tr><td>Delete Client Lose</td><td>' . $test->DeleteClientLose() . "</td></tr>";
echo '<tr><td>Delete Server Multi Lose</td><td>' . $test->DeleteServerMultiLose() . "</td></tr>";
echo '<tr><td>Delete Client Multi Lose</td><td>' . $test->DeleteClientMultiLose() . "</td></tr>";

echo '</table>';

?>