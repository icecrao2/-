import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const App());
}

class App extends StatefulWidget {
  const App({super.key});
  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  static const int _tickIntervalMs = 16; // setState 주기(60Hz≈16ms)
  static const Duration _testDuration = Duration(seconds: 20);
  static const double _frameBudgetMs = 16.7; // 60Hz 예산(120Hz면 8.3으로 변경)

  Timer? _ticker;
  Timer? _stopper;
  bool _running = false;
  int _counter = 0;

  final _isVisible = ValueNotifier(false);

  void _startTest() {
    if (_running) return;
    _running = true;
    _counter = 0;

    _isVisible.value = false;

    // 통계 초기화 및 시작
    TotalSpanStats.instance
      ..reset()
      ..start(frameBudgetMs: _frameBudgetMs);

    debugPrint('[Test] started: tick=${_tickIntervalMs}ms, duration=${_testDuration.inSeconds}s');

    // 주기적 setState
    _ticker = Timer.periodic(const Duration(milliseconds: _tickIntervalMs), (_) {
      if (!_running) return;

      _isVisible.value = !_isVisible.value;
      // setState(() {
      //   _counter++; // 부모 갱신
      // });
    });

    // 정확히 20초 뒤 종료
    _stopper = Timer(_testDuration, _stopTestAndReport);
  }

  void _stopTestAndReport() {
    if (!_running) return;
    _running = false;

    _ticker?.cancel();
    _stopper?.cancel();
    _ticker = null;
    _stopper = null;

    // 수집 종료 및 요약 로그
    TotalSpanStats.instance.stop();
    TotalSpanStats.instance.logSummary();
    debugPrint('[Test] finished.');
    setState(() {}); // 버튼 상태 갱신
  }

  @override
  void dispose() {
    _ticker?.cancel();
    _stopper?.cancel();
    if (_running) {
      TotalSpanStats.instance.stop();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // 여기의 child를 Offstage/Opacity/Visibility/if-else로 감싸며 실험하면 됨.
    final Widget heavyChild = ValueListenableBuilder(
        valueListenable: _isVisible,
        builder: (_, isVisible, __) {
          return Visibility(
            visible: isVisible,
            child: SingleChildScrollView(
              child: Column(
                children: List.generate(100, (idx) => Container(
                  width: 200,
                  height: 200,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(border: Border.all(color: Colors.black12)),
                  child: Text(
                    'frames: $_counter',
                    textAlign: TextAlign.center,
                  ),
                ),),
              ),
            )
          );
        }
    );

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('FrameTiming 20s Test')),
        body: Center(child: heavyChild),
        bottomNavigationBar: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: ElevatedButton(
              onPressed: _running ? null : _startTest,
              child: Text(_running ? 'Running… (20s)' : 'Start 20s Test'),
            ),
          ),
        ),
      ),
    );
  }
}












/// totalSpan(한 프레임의 전체 길이)만 수집/요약
class TotalSpanStats {
  TotalSpanStats._();
  static final TotalSpanStats instance = TotalSpanStats._();

  final List<double> _totalMs = <double>[];
  int _missed = 0;
  double _budgetMs = 16.7;
  bool _running = false;

  void Function(List<FrameTiming>)? _cb;

  void start({double frameBudgetMs = 16.7}) {
    if (_running) return;
    _running = true;
    _budgetMs = frameBudgetMs;

    _cb = (List<FrameTiming> timings) {
      for (final t in timings) {
        final total = t.totalSpan.inMicroseconds / 1000.0;
        _totalMs.add(total);
        if (total > _budgetMs) _missed++;
      }
    };

    SchedulerBinding.instance.addTimingsCallback(_cb!);
  }

  void stop() {
    if (!_running) return;
    SchedulerBinding.instance.removeTimingsCallback(_cb!);
    _cb = null;
    _running = false;
  }

  void reset() {
    _totalMs.clear();
    _missed = 0;
  }

  void logSummary() => debugPrint(summary());

  String summary() {
    final n = _totalMs.length;
    if (n == 0) return '[TotalSpanStats] No frames recorded.';

    double avg(List<double> a) => a.reduce((x, y) => x + y) / a.length;
    double p95(List<double> a) {
      final b = List<double>.from(a)..sort();
      final idx = (0.95 * (b.length - 1)).ceil();
      return b[idx];
    }

    final totalAvg = avg(_totalMs);
    final totalP95 = p95(_totalMs);
    final totalMax = _totalMs.reduce(math.max);
    final totalMin = _totalMs.reduce(math.min);
    final missedRatio = (_missed / n) * 100.0;

    return '[TotalSpanStats] frames=$n, '
        'totalSpan(avg)=${totalAvg.toStringAsFixed(2)}ms, '
        'totalSpan(p95)=${totalP95.toStringAsFixed(2)}ms, '
        'totalSpan(max)=${totalMax.toStringAsFixed(2)}ms, '
        'totalSpan(min)=${totalMin.toStringAsFixed(2)}ms, '
        'missed=${_missed} (${missedRatio.toStringAsFixed(2)}%, '
        'budget=${_budgetMs.toStringAsFixed(2)}ms)';
  }
}
